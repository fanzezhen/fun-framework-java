package com.github.fanzezhen.fun.framework.mp.trace.interceptor;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ClassUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.parser.JsqlParserGlobal;
import com.github.fanzezhen.fun.framework.log.config.LogSpringConfig;
import com.github.fanzezhen.fun.framework.mp.base.IBaseMapper;
import com.github.fanzezhen.fun.framework.mp.base.entity.BaseEntity;
import com.github.fanzezhen.fun.framework.mp.config.properties.MybatisProperties;
import com.github.fanzezhen.fun.framework.mp.trace.model.bo.TraceRuleBO;
import com.github.fanzezhen.fun.framework.mp.trace.model.entity.TraceDO;
import com.github.fanzezhen.fun.framework.mp.trace.model.entity.TraceDetailDO;
import com.github.fanzezhen.fun.framework.mp.trace.service.ITraceDetailService;
import com.github.fanzezhen.fun.framework.mp.trace.service.ITraceService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author fanzezhen
 * @since 3.4.3.1
 */
@Slf4j
@Component
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
})
@SuppressWarnings("unused")
public class TraceInterceptor implements Interceptor {
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private MybatisProperties mybatisProperties;
    @Resource
    private ThreadPoolTaskExecutor traceAsyncExecutor;
    private ITraceService traceService;
    private ITraceDetailService traceDetailService;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result;
        if (invocation.getArgs()[0] instanceof MappedStatement mappedStatement) {
            String statementId = mappedStatement.getId();
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
            if (SqlCommandType.INSERT.equals(sqlCommandType)) {
                result = invocation.proceed();
                try {
                    Object parameter = invocation.getArgs()[1];
                    BoundSql boundSql = mappedStatement.getBoundSql(parameter);
                    String sql = boundSql.getSql();
                    Statement statement = JsqlParserGlobal.parse(sql);
                    if (!(statement instanceof Insert)) {
                        return result;
                    }
                    String tableName = ((Insert) statement).getTable().getName();
                    TraceRuleBO traceRuleBO = getTraceService().getTraceRule(tableName);
                    if (traceRuleBO == null) {
                        return result;
                    }
                    // 暂不记录新增情况下的详情
                    List<TraceDetailDO> detailList = buildDetailList(tableName, traceRuleBO, parameter);
                    if (!detailList.isEmpty()) {
                        traceAsyncExecutor.execute(()->getTraceDetailService().saveOrUpdateBatch(detailList));
                    }
                } catch (Exception e) {
                    log.warn("", e);
                }
                return result;
            }
        }
        return invocation.proceed();
    }

    private List<TraceDetailDO> buildDetailList(String tableName, TraceRuleBO traceRuleBO, Object parameter)
        throws IllegalAccessException {
        TraceRuleBO parentTraceRuleBO = getTraceService().getTraceRule(traceRuleBO.getParent());
        TableInfo tableInfo = mybatisProperties.getTableInfo(tableName);
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        Map<String, Field> columnFieldMap = fieldList.stream().collect(Collectors.toMap(tableFieldInfo ->
            tableFieldInfo.getColumn().toUpperCase(), TableFieldInfo::getField));
        List<TraceDetailDO> detailList = new ArrayList<>();
        Map<Serializable, String> traceIdMap = new HashMap<>(2);
        if (parameter instanceof BaseEntity parameterBaseEntity) {
            detailList.addAll(
                buildDetailList(
                    tableName,
                    traceRuleBO,
                    parentTraceRuleBO,
                    parameterBaseEntity,
                    columnFieldMap,
                    traceIdMap));
        } else if (parameter instanceof MapperMethod.ParamMap) {
            Object arg0 = ((MapperMethod.ParamMap<?>) parameter).get("arg0");
            if (arg0 instanceof List) {
                for (Object argItem : (List<?>) arg0) {
                    if (argItem instanceof BaseEntity argItemBaseEntity) {
                        detailList.addAll(
                            buildDetailList(
                                tableName,
                                traceRuleBO,
                                parentTraceRuleBO,
                                argItemBaseEntity,
                                columnFieldMap,
                                traceIdMap));
                    }
                }
            }
        }
        return detailList;
    }

    private List<TraceDetailDO> buildDetailList(String tableName,
                                                TraceRuleBO traceRuleBO,
                                                TraceRuleBO parentTraceRuleBO,
                                                BaseEntity entity,
                                                Map<String, Field> columnFieldMap,
                                                Map<Serializable, String> traceIdMap)
        throws IllegalAccessException {
        List<TraceDetailDO> detailList = new ArrayList<>();
        String id = entity.getId();
        if (parentTraceRuleBO != null) {
            TableInfo parentTableInfo = mybatisProperties.getTableInfo(traceRuleBO.getParent());
            String parentKey = traceRuleBO.getParentKey();
            Object parentPkObj = columnFieldMap.get(parentKey).get(entity);
            if (parentTableInfo != null && CharSequenceUtil.isNotBlank(parentKey) && parentPkObj instanceof Serializable parentPk) {
                Object nameValue = columnFieldMap.get(traceRuleBO.getNameKey()).get(entity);
                String name = nameValue != null ? nameValue.toString() : CharSequenceUtil.EMPTY;
                String namePrefix = traceRuleBO.getName() + name + "的";
                for (Map.Entry<String, Field> entry : columnFieldMap.entrySet()) {
                    String fieldName = traceRuleBO.getDetail().get(entry.getKey());
                    Object newValue = entry.getValue().get(entity);
                    if (fieldName == null || newValue == null) {
                        continue;
                    }
                    String traceId = traceIdMap.computeIfAbsent(parentPk, k -> {
                        String currentNamespace = parentTableInfo.getCurrentNamespace();
                        String pk = getTraceService().getPkByTraceAndBiz(MDC.get(LogSpringConfig.TRACE_ID), parentPk);
                        if (pk == null) {
                            IBaseMapper<?> parentDao = applicationContext.getBean(ClassUtil.loadClass(currentNamespace));
                            BaseEntity parent = parentDao.selectById(parentPk);
                            String parentValue = null;
                            Optional<TableFieldInfo> parentNameTableFieldInfo =
                                parentTableInfo.getFieldList().stream().filter(tableFieldInfo ->
                                        tableFieldInfo.getColumn().equalsIgnoreCase(parentTraceRuleBO.getNameKey()))
                                    .findAny();
                            if (parentNameTableFieldInfo.isPresent()) {
                                try {
                                    parentValue = parentNameTableFieldInfo.get().getField().get(parent).toString();
                                } catch (Exception e) {
                                    log.warn("", e);
                                }
                            }
                            TraceDO traceDO = new TraceDO()
                                .setName(parentTraceRuleBO.getName())
                                .setCode(traceRuleBO.getParent())
                                .setValue(parentValue)
                                .setType(SqlCommandType.UPDATE)
                                .setTraceId(MDC.get(LogSpringConfig.TRACE_ID))
                                .setBusinessId(parent.getId());
                            pk = getTraceService().save(traceDO);
                        }
                        return pk;
                    });
                    TraceDetailDO traceDetailDO = new TraceDetailDO()
                        .setTraceId(traceId)
                        .setBusinessId(id)
                        .setName(namePrefix + "“" + fieldName + "”")
                        .setCode(tableName + StrPool.DOT + entry.getKey())
                        .setNewValue((newValue instanceof JSON json) ? json.toJSONString() : newValue.toString());
                    detailList.add(traceDetailDO);
                }
            }
        } else {
            String value = columnFieldMap.get(traceRuleBO.getNameKey()).get(entity).toString();
            TraceDO traceDO = new TraceDO()
                .setName(traceRuleBO.getName())
                .setCode(tableName)
                .setValue(value)
                .setType(SqlCommandType.INSERT)
                .setTraceId(MDC.get(LogSpringConfig.TRACE_ID))
                .setBusinessId(id);
            String traceId = traceIdMap.computeIfAbsent(id, k -> getTraceService().save(traceDO));
        }
        return detailList;
    }

    private ITraceService getTraceService() {
        if (traceService != null) {
            return traceService;
        }
        traceService = applicationContext.getBean(ITraceService.class);
        return traceService;
    }

    private ITraceDetailService getTraceDetailService() {
        if (traceDetailService != null) {
            return traceDetailService;
        }
        traceDetailService = applicationContext.getBean(ITraceDetailService.class);
        return traceDetailService;
    }

}
