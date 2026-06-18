package com.github.fanzezhen.fun.framework.trace.service;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.github.fanzezhen.fun.framework.core.model.entity.IGenericEntity;
import com.github.fanzezhen.fun.framework.mp.base.entity.uuid.BaseEntity;
import com.github.fanzezhen.fun.framework.trace.model.bo.TraceRuleBO;
import lombok.SneakyThrows;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.SqlCommandType;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 数据追踪服务接口
 * <p>
 * 提供数据变更追踪的核心功能，包括追踪规则查询、追踪记录保存、明细数据处理等。
 * 支持泛型化设计，可适配不同主键类型和实体类型的追踪场景。
 * <p>
 * 泛型参数说明：
 * <ul>
 *   <li>P - 主键类型（如 Long、String、Integer）</li>
 *   <li>A - 追踪主表实体类型（如 TraceEntity）</li>
 *   <li>B - 追踪明细表实体类型（如 TraceDetailEntity）</li>
 * </ul>
 *
 * @param <P> 主键类型
 * @param <A> 追踪主表实体类型
 * @param <B> 追踪明细表实体类型
 *
 * @since 3.4.3.1
 */
public interface IFunTraceService<P extends Serializable, A extends IGenericEntity<P>, B extends IGenericEntity<P>> {
    /**
     * 获取指定表的追踪规则
     * <p>
     * 根据表名查询对应的追踪配置，如果表未配置追踪规则则返回 null。
     *
     * @param tableName 数据库表名
     * @return 追踪规则对象，未配置则返回 null
     */
    TraceRuleBO getTraceRule(final String tableName);

    /**
     * 批量保存追踪主表记录
     * <p>
     * 将数据变更的主记录（如操作类型、业务ID、变更摘要等）持久化到追踪主表。
     *
     * @param traces 追踪记录集合
     * @return 保存是否成功
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean addTrace(final Collection<A> traces);

    /**
     * 批量保存追踪明细记录
     * <p>
     * 将字段级的变更详情（如字段名、旧值、新值等）持久化到追踪明细表。
     *
     * @param details 追踪明细集合
     * @return 保存是否成功
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean addDetail(final Collection<B> details);

    /**
     * 为追踪明细设置关联的追踪主表ID
     * <p>
     * 建立明细记录与主记录的外键关联关系。
     *
     * @param detail  追踪明细对象
     * @param traceId 追踪主表ID
     * @return 设置后的明细对象
     */
    @SuppressWarnings("UnusedReturnValue")
    B setTraceId(final B detail, final P traceId);

    /**
     * 创建追踪主表实体对象
     * <p>
     * 构造一条新的追踪主记录，记录数据变更的核心信息。
     *
     * @param type       SQL 操作类型（INSERT、UPDATE、DELETE）
     * @param businessId 业务主键ID（被追踪数据的主键）
     * @param name       业务对象名称（如"用户信息"、"订单数据"）
     * @param code       追踪标识（通常为表名）
     * @param value      业务对象标识值（如用户名、订单号）
     * @return 追踪主表实体对象
     */
    A newTrace(final SqlCommandType type, final String businessId, final String name, final String code, final String value);

    /**
     * 创建追踪明细实体对象
     * <p>
     * 构造一条新的追踪明细记录，记录单个字段的变更详情。
     *
     * @param businessId 业务主键ID（被追踪数据的主键）
     * @param name       字段显示名称（如"邮箱"、"手机号"）
     * @param code       字段标识（数据库列名）
     * @param newValue   字段新值
     * @return 追踪明细实体对象
     */
    B newTraceDetail(final String businessId, final String name, final String code, final String newValue);

    /**
     * 保存追踪数据（主表 + 明细表）
     * <p>
     * 先保存追踪主表记录，再将主表ID设置到明细记录中并保存。
     * 保证主表和明细表的数据一致性。
     *
     * @param traceMap 追踪数据映射表<br>
     *                 key: 业务主键ID<br>
     *                 value: Pair对象，左值为追踪主记录，右值为对应的明细记录列表
     */
    default void save(final Map<Serializable, Pair<A, List<B>>> traceMap) {
        if (MapUtil.isEmpty(traceMap)) {
            return;
        }
        List<A> traceList = traceMap.values().stream().map(Pair::getKey).filter(trace -> trace.getId() == null).toList();
        if (!traceList.isEmpty()) {
            addTrace(traceList);
        }
        List<B> detailList = traceMap.values().stream().flatMap(pair -> {
            List<B> list = pair.getValue();
            if (list == null) {
                return Stream.empty();
            }
            for (B detail : list) {
                setTraceId(detail, pair.getKey().getId());
            }
            return list.stream();
        }).toList();
        if (!detailList.isEmpty()) {
            addDetail(detailList);
        }
    }

    /**
     * 处理 INSERT 操作的数据追踪
     * <p>
     * 拦截 MyBatis 的 INSERT 操作，根据追踪规则提取变更数据并保存到追踪表。
     * 支持单条插入和批量插入两种场景。
     *
     * @param tableName   数据库表名
     * @param traceRuleBO 追踪规则对象
     * @param parameter   MyBatis 参数对象（可能是实体对象或 ParamMap）
     */
    @SneakyThrows
    default void traceOfInsert(final String tableName, final TraceRuleBO traceRuleBO, final Object parameter) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(tableName);
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        Map<String, Field> columnFieldMap = fieldList.stream().collect(Collectors.toMap(tableFieldInfo ->
            tableFieldInfo.getColumn().toUpperCase(), TableFieldInfo::getField));
        Map<Serializable, Pair<A, List<B>>> traceMap = new HashMap<>();
        if (parameter instanceof BaseEntity parameterBaseEntity) {
            buildTrace(
                tableName,
                traceRuleBO,
                parameterBaseEntity,
                columnFieldMap,
                traceMap);
        } else if (parameter instanceof MapperMethod.ParamMap<?> paramMap) {
            Object arg0 = paramMap.get("arg0");
            if (arg0 instanceof List<?> argList) {
                for (Object argItem : argList) {
                    if (argItem instanceof BaseEntity argItemBaseEntity) {
                        buildTrace(
                            tableName,
                            traceRuleBO,
                            argItemBaseEntity,
                            columnFieldMap,
                            traceMap);
                    }
                }
            }
        }
        save(traceMap);
    }

    /**
     * 构建单条数据的追踪记录
     * <p>
     * 根据追踪规则从实体对象中提取需要追踪的字段数据，构造追踪主表和明细表记录。
     * 如果同一业务ID已存在追踪记录，则追加到现有记录中。
     *
     * @param tableName      数据库表名
     * @param traceRuleBO    追踪规则对象
     * @param entity         被追踪的实体对象
     * @param columnFieldMap 数据库列名到字段对象的映射（用于反射获取字段值）
     * @param traceMap       追踪数据收集器（业务ID -> 追踪记录）
     * @return 更新后的追踪数据收集器
     * @throws IllegalAccessException 反射访问字段失败时抛出
     */
    @SuppressWarnings("UnusedReturnValue")
    default Map<Serializable, Pair<A, List<B>>> buildTrace(final String tableName,
                                                           final TraceRuleBO traceRuleBO,
                                                           final BaseEntity entity,
                                                           final Map<String, Field> columnFieldMap,
                                                           Map<Serializable, Pair<A, List<B>>> traceMap)
        throws IllegalAccessException {
        if (traceMap == null) {
            traceMap = new HashMap<>();
        }
        String id = entity.getId();
        String value = columnFieldMap.get(traceRuleBO.getNameKey()).get(entity).toString();
        A trace = newTrace(SqlCommandType.INSERT, id, traceRuleBO.getName(), tableName, value);
        traceMap.computeIfAbsent(id, t -> Pair.of(trace, new ArrayList<>()));
        return traceMap;
    }

}
