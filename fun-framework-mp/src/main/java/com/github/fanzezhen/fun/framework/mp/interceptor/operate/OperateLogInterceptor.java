package com.github.fanzezhen.fun.framework.mp.interceptor.operate;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.fanzezhen.fun.framework.core.context.SysContextHolder;
import com.github.fanzezhen.fun.framework.core.annotion.OperateLog;
import com.github.fanzezhen.fun.framework.core.annotion.OperateLogMapper;
import com.github.fanzezhen.fun.framework.core.dict.AbstractDict;
import com.github.fanzezhen.fun.framework.mp.enums.log.OperationLogTypeEnum;
import com.github.fanzezhen.fun.framework.mp.enums.TableFieldEnum;
import com.github.fanzezhen.fun.framework.mp.interceptor.operate.facade.LogFacade;
import com.github.fanzezhen.fun.framework.mp.interceptor.operate.model.LogOperationDetailDto;
import com.github.fanzezhen.fun.framework.mp.interceptor.operate.model.LogOperationDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

/**
 * @author zezhen.fan
 */
@Slf4j
@Component
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
})
@SuppressWarnings("unused")
@ConditionalOnBean(LogFacade.class)
public class OperateLogInterceptor implements Interceptor {
    private static final String MAPPER_UPDATE_METHOD_PARAM_MAP_ET_KEY = "et";
    private static final String MAPPER_DELETE_METHOD_PARAM_MAP_EW_KEY = "ew";
    private static final String NAME_SUFFIX_DTO = "Dto";
    private static final String NAME_SUFFIX_SERVICE_IMPL = "ServiceImpl";
    @Value("${log.operate.check-mapper:false}")
    private boolean needCheckMapper;
    @Resource
    private LogFacade logFacade;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = null;
        Object arg = getArg(invocation);
        OperateLog operateLog = getOperateLog(arg);
        if (operateLog == null) {
            result = invocation.proceed();
            return result;
        }
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        String statementId = mappedStatement.getId();
        String type = statementId.substring(0, statementId.lastIndexOf("."));
        Class<?> mapperClass = Class.forName(type);
        if (needCheckMapper) {
            if (!mapperClass.isAnnotationPresent(OperateLogMapper.class)) {
                result = invocation.proceed();
                return result;
            }
        }
        AbstractDict dict = ReflectUtil.invokeStatic(
            ReflectUtil.getPublicMethod(operateLog.dictClass(), AbstractDict.INSTANCE_METHOD_NAME));
        boolean isAllFields = operateLog.isAllFields();
        String[] fieldNames = operateLog.fieldNameFilters();
        List<String> fieldNameList = Arrays.asList(fieldNames);
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        String tableName = operateLog.tableName();
        if (CharSequenceUtil.isBlank(tableName)) {
            tableName = getTableNameByClass(arg.getClass());
        }
        LogOperationDto logOperation = insertLog(String.valueOf(ReflectUtil.getFieldValue(arg, "id")),
            sqlCommandType.ordinal(), tableName, null);
        switch (sqlCommandType) {
            case INSERT -> {
                result = invocation.proceed();
                dealOnlyNewValueLog(dict, operateLog, sqlCommandType.ordinal(), arg);
            }
            case UPDATE -> {
                IService<?> service = getService(operateLog.serviceBeanName(), arg.getClass());
                if (service == null) {
                    // 无法获取旧值，按照新增执行（operateType为更新）
                    result = invocation.proceed();
                    dealOnlyNewValueLog(dict, operateLog, sqlCommandType.ordinal(), arg);
                    break;
                }
                List<LogOperationDetailDto> detailLogList = new ArrayList<>();
                //更新之前的PO参数
                Object oldBean = service.getById((Serializable) ReflectUtil.getFieldValue(arg, TableFieldEnum.PK.field));
                dict.getDict().forEach((key, name) -> {
                    if (!isAllFields && !fieldNameList.contains(key)) {
                        return;
                    }
                    Object oldFieldValue = ReflectUtil.getFieldValue(oldBean, key);
                    Object newFieldValue = ReflectUtil.getFieldValue(arg, key);
                    if (null != newFieldValue && !newFieldValue.equals(oldFieldValue)) {
                        LogOperationDetailDto detailLog = new LogOperationDetailDto();
                        detailLog.setLogId(logOperation.getId());
                        String tableColumn = CharSequenceUtil.toUnderlineCase(key);
                        detailLog.setTableColumn(tableColumn);
                        detailLog.setColumnName(name);
                        //新增只有新值
                        String newValue = String.valueOf(newFieldValue);
                        if (CharSequenceUtil.isNotEmpty(newValue)) {
                            detailLog.setNewValue(newValue);
                            detailLog.setOldValue(String.valueOf(oldFieldValue));
                            detailLogList.add(detailLog);
                        }
                    }
                });
                addLogOperateDetailBatch(detailLogList);
            }
            default -> {
            }
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    private LogOperationDto insertLog(String bizId, int operateType, String tableName, String comment) {
        LogOperationDto logOperation = new LogOperationDto();
        logOperation.setBizId(bizId);
        logOperation.setTableName(tableName);
        logOperation.setOperationType(OperationLogTypeEnum.getOrDefaultByType(operateType));
        logOperation.setAppCode(SysContextHolder.getCurrentAppCode());
        logOperation.setOperationUsername(SysContextHolder.getUserName());
        logOperation.setRemark(comment);
        logFacade.addLogOperate(logOperation);
        return logOperation;
    }

    private void addLogOperateDetailBatch(Collection<LogOperationDetailDto> logOperationDetails) {
            logFacade.addLogOperateDetailBatch(logOperationDetails);
    }

    /**
     * 处理只有新值的日志
     *
     * @param dict        字典
     * @param operateLog  日志注解
     * @param operateType 操作类型
     * @param arg         数据库入参
     */
    private void dealOnlyNewValueLog(AbstractDict dict,
                                     OperateLog operateLog,
                                     int operateType,
                                     Object arg) {
        String tableName = operateLog.tableName();
        if (CharSequenceUtil.isBlank(tableName)) {
            tableName = getTableNameByClass(arg.getClass());
        }
        String comment = null;
        if (operateType != SqlCommandType.INSERT.ordinal()) {
            comment = "未能成功加载旧值";
        }
        LogOperationDto logOperation = insertLog(String.valueOf(ReflectUtil.getFieldValue(arg, "id")), operateType, tableName, comment);
        List<LogOperationDetailDto> detailLogList = new ArrayList<>();
        boolean isAllFields = operateLog.isAllFields();
        String[] fieldNames = operateLog.fieldNameFilters();
        List<String> fieldNameList = Arrays.asList(fieldNames);
        dict.getDict().forEach((key, name) -> {
            if (!isAllFields && !fieldNameList.contains(key)) {
                return;
            }
            Object fieldValue = ReflectUtil.getFieldValue(arg, key);
            if (null != fieldValue) {
                LogOperationDetailDto detailLog = new LogOperationDetailDto();
                detailLog.setLogId(logOperation.getId());
                String tableColumn = CharSequenceUtil.toUnderlineCase(key);
                detailLog.setTableColumn(tableColumn);
                detailLog.setColumnName(name);
                //新增只有新值
                String newValue = String.valueOf(fieldValue);
                if (CharSequenceUtil.isNotEmpty(newValue)) {
                    detailLog.setNewValue(newValue);
                    detailLogList.add(detailLog);
                }
            }
        });
        addLogOperateDetailBatch(detailLogList);
    }

    @SuppressWarnings("all")
    private Object getArg(Invocation invocation) {
        Object arg = null;
        try {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
            switch (sqlCommandType) {
                case INSERT:
                    arg = invocation.getArgs()[1];
                    break;
                case UPDATE:
                    arg = ((Map<String, Object>) invocation.getArgs()[1]).get(MAPPER_UPDATE_METHOD_PARAM_MAP_ET_KEY);
                    break;
                case DELETE:
                    arg = ((Map<String, Object>) invocation.getArgs()[1]).get(MAPPER_DELETE_METHOD_PARAM_MAP_EW_KEY);
                    break;
                default:
            }
        } catch (Throwable throwable) {
            log.warn("", throwable);
        }
        return arg;
    }

    private String getTableNameByClass(Class<?> clazz) {
        return CharSequenceUtil.toUnderlineCase(CharSequenceUtil.strip(
            CharSequenceUtil.subAfter(clazz.getName(), StrPool.C_DOT, true), CharSequenceUtil.EMPTY, NAME_SUFFIX_DTO));
    }

    private String getServiceBeanNameByBeanClass(Class<?> clazz) {
        return CharSequenceUtil.lowerFirst(CharSequenceUtil.strip(CharSequenceUtil.subAfter(clazz.getName(), StrPool.C_DOT, true),
            CharSequenceUtil.EMPTY, NAME_SUFFIX_DTO)) + NAME_SUFFIX_SERVICE_IMPL;
    }

    private OperateLog getOperateLog(Object arg) {
        return arg == null ? null : arg.getClass().getAnnotation(OperateLog.class);
    }

    private IService<?> getService(String serviceBeanName, Class<?> clazz) {
        try {
            if (CharSequenceUtil.isBlank(serviceBeanName)) {
                serviceBeanName = getServiceBeanNameByBeanClass(clazz);
            }
            return CharSequenceUtil.isBlank(serviceBeanName) ? null : SpringUtil.getBean(serviceBeanName);
        } catch (Exception exception) {
            log.warn("", exception);
        }
        return null;
    }
}
