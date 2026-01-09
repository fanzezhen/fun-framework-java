package com.github.fanzezhen.fun.framework.data.elasticsearch7.config;

import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.github.fanzezhen.fun.framework.core.data.model.IColumnDeserializer;
import com.github.fanzezhen.fun.framework.core.log.base.support.FunLogHelper;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.config.FunElasticsearchProperties;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IElasticsearchResultDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IResponseDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.serializer.DefaultDocumentSerializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.serializer.IDocumentSerializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.template.BaseMultiDatasourceElasticsearchTemplate;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.template.ElasticsearchMultiDataSourceTemplateImpl;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author fanzezhen
 */
@Configuration
@EnableConfigurationProperties(FunElasticsearchProperties.class)
@ComponentScan("com.github.fanzezhen.fun.framework.data.elasticsearch7")
public class FunElasticsearch7AutoConfiguration {

    @Resource
    private FunElasticsearchProperties funElasticsearchProperties;
    @Resource
    private FunLogHelper funLogHelper;
    @Resource
    private List<IColumnDeserializer> columnDeserializerList;
    @Resource
    private List<IElasticsearchResultDeserializer> elasticsearchResultDeserializerList;
    @Getter
    static JacksonJsonpMapper jacksonJsonpMapper;

    public FunElasticsearch7AutoConfiguration(@Autowired(required = false) JacksonJsonpMapper jacksonJsonpMapper) {
        setJacksonJsonpMapper(jacksonJsonpMapper == null ? new JacksonJsonpMapper() : jacksonJsonpMapper);
    }

    @Bean
    @ConditionalOnMissingBean(BaseMultiDatasourceElasticsearchTemplate.class)
    public IDocumentSerializer funDefaultDocumentSerializer() {
        return new DefaultDocumentSerializer();
    }

    @Bean
    @ConditionalOnMissingBean(BaseMultiDatasourceElasticsearchTemplate.class)
    public BaseMultiDatasourceElasticsearchTemplate funMultiDatasourceElasticsearchTemplate(
        List<IDocumentSerializer> documentSerializerList,
        List<IResponseDeserializer> responseDeserializerList
    ) {
        return new ElasticsearchMultiDataSourceTemplateImpl(
            funElasticsearchProperties,
            funLogHelper,
            documentSerializerList,
            responseDeserializerList);
    }

    static void setJacksonJsonpMapper(JacksonJsonpMapper jacksonJsonpMapper) {
        FunElasticsearch7AutoConfiguration.jacksonJsonpMapper = jacksonJsonpMapper;
    }
}
