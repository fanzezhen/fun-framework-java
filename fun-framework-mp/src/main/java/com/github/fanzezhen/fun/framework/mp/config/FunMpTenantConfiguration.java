package com.github.fanzezhen.fun.framework.mp.config;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.github.fanzezhen.fun.framework.core.context.ContextHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.Resource;

/**
 * @author fanzezhen
 */
@Configuration
@ConditionalOnProperty(value = "fun.mp.tenant.enabled", havingValue = "true")
public class FunMpTenantConfiguration {
    @Resource
    private FunMpProperties funMpProperties;

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则
     */
    @Bean
    @ConditionalOnMissingBean
    public TenantLineInnerInterceptor tenantLineInnerInterceptor() {
        return new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                return new StringValue(ContextHolder.getTenantId());
            }

            @Override
            public boolean ignoreTable(String tableName) {
                if (funMpProperties.getTenant().getIgnoreTenantTables() == null) {
                    return false;
                }
                for (String ignoreTenantTable : funMpProperties.getTenant().getIgnoreTenantTables()) {
                    if (tableName.equalsIgnoreCase(ignoreTenantTable)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

}
