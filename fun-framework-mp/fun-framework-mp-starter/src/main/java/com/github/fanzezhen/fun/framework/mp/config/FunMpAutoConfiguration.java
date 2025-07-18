package com.github.fanzezhen.fun.framework.mp.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fanzezhen.fun.framework.core.context.properties.FunCoreContextProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author fanzezhen
 */
@Configuration
@EnableConfigurationProperties(FunMpProperties.class)
@ComponentScan("com.github.fanzezhen.fun.framework.mp")
public class FunMpAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        return new MybatisPlusInterceptor();
    }

    /**
     * 用于序列化枚举值为数据库存储值
     *
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    @ConditionalOnMissingBean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    }

    @Bean
    @ConditionalOnMissingBean
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
    @ConditionalOnMissingBean
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
