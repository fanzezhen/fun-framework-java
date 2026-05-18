package com.github.fanzezhen.fun.framework.mp.config;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * MyBatis-Plus 自动配置类
 * <p>
 * 提供 MyBatis-Plus 的核心自动配置，包括拦截器和 SQL 注入器。
 * 主要功能：
 * <ul>
 *   <li>配置 MybatisPlusInterceptor 拦截器</li>
 *   <li>配置 SQL 注入器，支持批量插入等扩展方法</li>
 * </ul>
 */
@Configuration
@EnableConfigurationProperties(FunMpProperties.class)
@ComponentScan("com.github.fanzezhen.fun.framework.mp")
public class FunMpAutoConfiguration {
    /**
     * 创建 MyBatis-Plus 拦截器
     * <p>
     * 如果容器中不存在 MybatisPlusInterceptor，则创建默认实例。
     * 使用者可以通过配置自定义的 MybatisPlusInterceptor Bean 来覆盖默认配置。
     *
     * @return MyBatis-Plus 拦截器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        return new MybatisPlusInterceptor();
    }

    /**
     * 创建 SQL 注入器
     * <p>
     * 扩展 MyBatis-Plus 的默认 SQL 注入器，添加批量插入方法。
     * 批量插入时会排除标记为 UPDATE 填充策略的字段。
     *
     * @return SQL 注入器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public ISqlInjector injector() {
        return new DefaultSqlInjector() {
            @Override
            public List<AbstractMethod> getMethodList(
                final org.apache.ibatis.session.Configuration configuration,
                final Class<?> mapperClass,
                final TableInfo tableInfo) {
                List<AbstractMethod> methodList = super.getMethodList(configuration, mapperClass, tableInfo);
                // 批量添加插件，排除 UPDATE 填充字段
                methodList.add(new InsertBatchSomeColumn(i -> i.getFieldFill() != FieldFill.UPDATE));
                return methodList;
            }
        };
    }

}
