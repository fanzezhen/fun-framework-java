package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.template;

import com.github.fanzezhen.fun.framework.core.log.base.support.FunLogHelper;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.config.FunElasticsearchProperties;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IResponseDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.serializer.IDocumentSerializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.template.BaseMultiDatasourceElasticsearchTemplate;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.template.IElasticsearchTemplate;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.config.FunElasticsearch7AutoConfiguration;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ElasticsearchMultiDataSourceTemplateImpl extends BaseMultiDatasourceElasticsearchTemplate {

    public ElasticsearchMultiDataSourceTemplateImpl(FunElasticsearchProperties funElasticsearchProperties,
                                                    FunLogHelper funLogHelper,
                                                    List<IDocumentSerializer> documentSerializerList,
                                                    List<IResponseDeserializer> responseDeserializerList) {
        super(funElasticsearchProperties, funLogHelper, documentSerializerList, responseDeserializerList);
    }

    /**
     * 通过配置创建一个客户端
     *
     */
    @Override
    protected IElasticsearchTemplate createTemplate(FunElasticsearchProperties.Config config) {
        return new ElasticsearchTemplateImpl(config, funLogHelper, FunElasticsearch7AutoConfiguration.getJacksonJsonpMapper(), documentSerializerList, responseDeserializerList);
    }
}
