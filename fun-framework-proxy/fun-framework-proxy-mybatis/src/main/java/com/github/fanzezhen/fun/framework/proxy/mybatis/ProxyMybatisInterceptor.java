package com.github.fanzezhen.fun.framework.proxy.mybatis;

import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * MyBatis查询结果代理拦截器
 * <p>
 * 拦截MyBatis的查询方法，通过ProxyHelper对查询结果进行装饰处理，
 * 实现字段值的自动代理（如将内网图片URL转换为外网可访问URL）。
 * <p>
 * <b>拦截点：</b>Executor.query方法（所有查询操作）
 * <p>
 * <b>性能考虑：</b>仅处理查询结果的装饰，不影响SQL执行，但会遍历结果对象的字段
 *
 * @since 3.4.3.5
 */
@Slf4j
@Component
@ConditionalOnBean({ProxyHelper.class})
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class ProxyMybatisInterceptor implements Interceptor {
    @Resource
    private ProxyHelper proxyHelper;

    /**
     * 拦截查询方法，对查询结果进行代理处理
     *
     * @param invocation 调用信息
     * @return 处理后的查询结果
     * @throws Throwable 执行异常
     */
    @Override
    public Object intercept(final Invocation invocation) throws Throwable {
        Object result = invocation.proceed();
        return proxyHelper.decorateByAnnotation(result);
    }

}

