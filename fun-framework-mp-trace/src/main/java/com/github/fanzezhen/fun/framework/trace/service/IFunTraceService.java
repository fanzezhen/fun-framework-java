package com.github.fanzezhen.fun.framework.trace.service;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.github.fanzezhen.fun.framework.log.config.LogSpringConfig;
import com.github.fanzezhen.fun.framework.mp.base.IBaseMapper;
import com.github.fanzezhen.fun.framework.mp.base.entity.BaseEntity;
import com.github.fanzezhen.fun.framework.trace.model.bo.TraceRuleBO;
import lombok.SneakyThrows;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.MDC;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 痕迹表 服务类
 *
 * @author fanzezhen
 * @since 3.4.3.1
 */
public interface IFunTraceService<A extends BaseEntity, B> {
    /**
     * 获取痕迹规则
     */
    TraceRuleBO getTraceRule(String tableName);

    /**
     * 根据上下文id和业务id查询
     */
    A getByTraceAndBiz(String traceId, Serializable businessId);

    /**
     * 保存痕迹记录
     */
    boolean addTrace(Collection<A> traces);

    /**
     * 保存痕迹明细
     */
    boolean addDetail(Collection<B> details);

    /**
     * 为明细数据设置痕迹id
     */
    B setTraceId(B detail, String traceId);

    /**
     * 创建痕迹实体
     *
     * @param type       痕迹类型
     * @param businessId 业务id
     * @param name       名称
     * @param code       标识
     * @param value      值
     */
    A newTrace(SqlCommandType type, String businessId, String name, String code, String value);

    /**
     * 创建痕迹明细实体
     *
     * @param businessId 业务id
     * @param name       名称
     * @param code       标识
     * @param newValue   新值
     */
    B newTraceDetail(String businessId, String name, String code, String newValue);

    /**
     * 先保存痕迹主表， 再将主表id设置到明细中并保存明细数据
     *
     * @param traceMap ｛businessId: {trace: details}｝
     */
    default void save(Map<Serializable, Pair<A, List<B>>> traceMap) {
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
     * 处理INSERT场景的痕迹
     *
     * @param tableName   表名
     * @param traceRuleBO 痕迹规则
     * @param parameter   mybatis参数
     */
    @SneakyThrows
    default void traceOfInsert(String tableName, TraceRuleBO traceRuleBO, Object parameter) {
        TraceRuleBO parentTraceRuleBO = getTraceRule(traceRuleBO.getParent());
        TableInfo tableInfo = TableInfoHelper.getTableInfo(tableName);
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        Map<String, Field> columnFieldMap = fieldList.stream().collect(Collectors.toMap(tableFieldInfo ->
            tableFieldInfo.getColumn().toUpperCase(), TableFieldInfo::getField));
        Map<Serializable, Pair<A, List<B>>> traceMap = new HashMap<>();
        if (parameter instanceof BaseEntity parameterBaseEntity) {
            buildTrace(
                tableName,
                traceRuleBO,
                parentTraceRuleBO,
                parameterBaseEntity,
                columnFieldMap,
                traceMap);
        } else if (parameter instanceof MapperMethod.ParamMap) {
            Object arg0 = ((MapperMethod.ParamMap<?>) parameter).get("arg0");
            if (arg0 instanceof List) {
                for (Object argItem : (List<?>) arg0) {
                    if (argItem instanceof BaseEntity argItemBaseEntity) {
                        buildTrace(
                            tableName,
                            traceRuleBO,
                            parentTraceRuleBO,
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
     * 构建痕迹数据
     *
     * @param tableName         表名
     * @param traceRuleBO       痕迹规则
     * @param parentTraceRuleBO 父级痕迹规则
     * @param entity            当前实体
     * @param columnFieldMap    字段名称与字段的映射
     * @param traceMap          已收集的痕迹数据
     */
    default Map<Serializable, Pair<A, List<B>>> buildTrace(String tableName,
                                                           TraceRuleBO traceRuleBO,
                                                           TraceRuleBO parentTraceRuleBO,
                                                           BaseEntity entity,
                                                           Map<String, Field> columnFieldMap,
                                                           Map<Serializable, Pair<A, List<B>>> traceMap)
        throws IllegalAccessException {
        if (traceMap == null) {
            traceMap = new HashMap<>();
        }
        String id = entity.getId();
        if (parentTraceRuleBO != null) {
            TableInfo parentTableInfo = TableInfoHelper.getTableInfo(tableName);
            String parentKey = traceRuleBO.getParentKey();
            Object parentPkObj = columnFieldMap.get(parentKey).get(entity);
            if (parentTableInfo == null || CharSequenceUtil.isBlank(parentKey)) {
                String value = columnFieldMap.get(traceRuleBO.getNameKey()).get(entity).toString();
                A trace = newTrace(SqlCommandType.INSERT, id, traceRuleBO.getName(), tableName, value);
                traceMap.computeIfAbsent(id, t -> Pair.of(trace, new ArrayList<>()));
            } else if (parentPkObj instanceof Serializable parentPk) {
                Object nameValue = columnFieldMap.get(traceRuleBO.getNameKey()).get(entity);
                String name = nameValue != null ? nameValue.toString() : CharSequenceUtil.EMPTY;
                String namePrefix = traceRuleBO.getName() + name + "的";
                for (Map.Entry<String, Field> entry : columnFieldMap.entrySet()) {
                    String fieldName = traceRuleBO.getDetail().get(entry.getKey());
                    Object newValue = entry.getValue().get(entity);
                    if (fieldName == null || newValue == null) {
                        continue;
                    }
                    B traceDetail = newTraceDetail(id, namePrefix + "“" + fieldName + "”", tableName + StrPool.DOT + entry.getKey(), (newValue instanceof JSON json) ? json.toJSONString() : newValue.toString());
                    traceMap.computeIfAbsent(parentPk, k -> {
                        String currentNamespace = parentTableInfo.getCurrentNamespace();
                        A t = getByTraceAndBiz(MDC.get(LogSpringConfig.TRACE_ID), parentPk);
                        if (t != null) {
                            return Pair.of(t, new ArrayList<>());
                        }
                        BaseEntity parent = getParent(parentPk, currentNamespace);
                        String parentValue = null;
                        Optional<TableFieldInfo> parentNameTableFieldInfo =
                            parentTableInfo.getFieldList().stream().filter(tableFieldInfo ->
                                    tableFieldInfo.getColumn().equalsIgnoreCase(parentTraceRuleBO.getNameKey()))
                                .findAny();
                        if (parentNameTableFieldInfo.isPresent()) {
                            try {
                                parentValue = parentNameTableFieldInfo.get().getField().get(parent).toString();
                            } catch (Exception e) {
                                StaticLog.warn("", e);
                            }
                        }
                        return Pair.of(newTrace(SqlCommandType.UPDATE, parent.getId(), parentTraceRuleBO.getName(), traceRuleBO.getParent(), parentValue), new ArrayList<>());
                    }).getValue().add(traceDetail);
                }
            }
        }
        return traceMap;
    }

    /**
     * 获取父级实体
     *
     * @param parentPk         父级主键
     * @param currentNamespace 父级实体的mybatis命名空间
     */
    default BaseEntity getParent(Serializable parentPk, String currentNamespace) {
        IBaseMapper<?> parentDao = SpringUtil.getBean(ClassUtil.loadClass(currentNamespace));
        return parentDao.selectById(parentPk);
    }

}
