package com.github.fanzezhen.fun.framework.trace.interceptor;

import com.baomidou.mybatisplus.extension.parser.JsqlParserGlobal;
import com.github.fanzezhen.fun.framework.mp.config.properties.MybatisProperties;
import com.github.fanzezhen.fun.framework.trace.model.bo.TraceRuleBO;
import com.github.fanzezhen.fun.framework.trace.service.IFunTraceService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
    private ThreadPoolTaskExecutor funTraceThreadPoolTaskExecutor;
    @Setter
    private IFunTraceService funTraceService;

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
                    TraceRuleBO traceRuleBO = funTraceService.getTraceRule(tableName);
                    if (traceRuleBO == null) {
                        return result;
                    }
                    funTraceThreadPoolTaskExecutor.execute(() -> funTraceService.traceOfInsert(tableName, traceRuleBO, parameter));
                } catch (Exception e) {
                    log.warn("", e);
                }
                return result;
            }
        }
        return invocation.proceed();
    }

}
