package com.github.fanzezhen.fun.framework.proxy.mybatis;

import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.Resource;

/**
 * MyBatis 代理配置类
 * <p>
 * 自动配置 MyBatis 的代理类型处理器，实现数据库查询结果的自动URL转换
 *
 * @since 3.4.3.5
 */
@Configuration
@ConditionalOnBean({ProxyHelper.class})
public class ProxyJdbcConfig implements InitializingBean {

    @Resource
    private ProxyHelper proxyHelper;

    /**
     * 初始化 MyBatis 代理组件
     *
     * @throws Exception 初始化异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        ProxyFieldStringTypeHandler.initStatic(true, proxyHelper);
    }
}
