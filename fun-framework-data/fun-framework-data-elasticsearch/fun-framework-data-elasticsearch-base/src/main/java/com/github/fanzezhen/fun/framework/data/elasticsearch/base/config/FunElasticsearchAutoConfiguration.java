package com.github.fanzezhen.fun.framework.data.elasticsearch.base.config;

import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IResponseDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IElasticsearchResultDeserializer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * @author fanzezhen
 */
@Configuration
@EnableConfigurationProperties(FunElasticsearchProperties.class)
@ComponentScan("com.github.fanzezhen.fun.framework.data.elasticsearch")
public class FunElasticsearchAutoConfiguration {
    
    @Resource
    private FunElasticsearchProperties funElasticsearchProperties;
    @Resource
    private List<IElasticsearchResultDeserializer> elasticsearchResultDeserializerList;
    @Resource
    private List<IResponseDeserializer> elasticsearchResponseDeserializerList;
    @Getter
    static List<IElasticsearchResultDeserializer> staticResultDeserializerList = Collections.emptyList();
    @Getter
    static List<IResponseDeserializer> staticResponseDeserializerList = Collections.emptyList();
    
    @PostConstruct
    public void init(){
        setStaticResultDeserializerList(elasticsearchResultDeserializerList);
        setStaticResponseDeserializerList(elasticsearchResponseDeserializerList);
        for (FunElasticsearchProperties.Config config : funElasticsearchProperties.getConfigs()) {
            // 注入bean
        }
    }

    static void setStaticResultDeserializerList(List<IElasticsearchResultDeserializer> resultDeserializerList) {
        staticResultDeserializerList = resultDeserializerList;
    }

    static void setStaticResponseDeserializerList(List<IResponseDeserializer> staticResponseDeserializerList) {
        FunElasticsearchAutoConfiguration.staticResponseDeserializerList = staticResponseDeserializerList;
    }
}
