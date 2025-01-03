package com.github.fanzezhen.fun.framework.mp.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fanzezhen.fun.framework.core.context.SysContextHolder;
import com.github.fanzezhen.fun.framework.core.property.CommonCoreProperties;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zezhen.fan
 */
@EnableTransactionManagement
@Configuration
@MapperScan(basePackages = {"com.github.fanzezhen.*.mapper*", "${com.github.fanzezhen.fun.framework.mp.package:}"})
public class MybatisPlusConfig {
    @Resource
    private CommonCoreProperties commonCoreProperties;

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        String tenantId = SysContextHolder.getTenantId();
        CommonCoreProperties.Tenant tenantConfig = commonCoreProperties.getTenant();
        if (tenantConfig!=null&&tenantConfig.isEnabled()) {
            interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
                @Override
                public Expression getTenantId() {
                    return new StringValue(tenantId);
                }
                // 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
                @Override
                public boolean ignoreTable(String tableName) {
                    if (tenantConfig.getIgnoreTenantTables() == null){
                        return false;
                    }
                    for (String ignoreTenantTable : tenantConfig.getIgnoreTenantTables()) {
                        if (tableName.equalsIgnoreCase(ignoreTenantTable)){
                            return true;
                        }
                    }
                    return false;
                }
            }));
        }
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.H2));
        return interceptor;
    }


    @Configuration
    public class MyCustomConfig {

        /**
         * 动态地为已有的 mybatisPlusInterceptor 添加新的内部拦截器。
         */
        @Bean
        @ConditionalOnBean(MybatisPlusInterceptor.class)
        public MybatisPlusInterceptor extendMybatisPlusInterceptor(MybatisPlusInterceptor existingInterceptor) {
            // 添加分页拦截器
            existingInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

            // 如果有其他需要添加的拦截器，继续添加
            // existingInterceptor.addInnerInterceptor(anotherInnerInterceptor);

            return existingInterceptor;
        }
    }

    /**
     * 用于序列化枚举值为数据库存储值
     *
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    }

    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        // 序列化枚举值为数据库存储值
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteEnumUsingToString);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(fastJsonHttpMessageConverter);
    }

    /**
     * 注射器  
     */
    @Bean
    public ISqlInjector injector() {
        return new DefaultSqlInjector() {
            @Override
            public List<AbstractMethod> getMethodList(org.apache.ibatis.session.Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
                List<AbstractMethod> methodList =  super.getMethodList(configuration, mapperClass, tableInfo);
                // 批量添加插件
                methodList.add(new InsertBatchSomeColumn(i -> i.getFieldFill() != FieldFill.UPDATE));
                return methodList;
            }
        };
    }

}
