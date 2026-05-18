package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.template;

import com.github.fanzezhen.fun.framework.core.log.support.FunLogHelper;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.config.FunElasticsearchProperties;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IResponseDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.serializer.IDocumentSerializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.template.BaseMultiDatasourceElasticsearchTemplate;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.template.IElasticsearchTemplate;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.config.FunElasticsearch7AutoConfiguration;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Elasticsearch 多数据源模板实现类
 *
 * <p>提供多数据源 Elasticsearch 操作模板的具体实现，支持根据配置动态创建不同数据源的模板实例。
 */
@Slf4j
public class ElasticsearchMultiDataSourceTemplateImpl extends BaseMultiDatasourceElasticsearchTemplate {

    /**
     * 构造函数
     *
     * @param funElasticsearchProperties Elasticsearch 配置属性
     * @param funLogHelper 日志辅助类
     * @param documentSerializerList 文档序列化器列表
     * @param responseDeserializerList 响应反序列化器列表
     */
    public ElasticsearchMultiDataSourceTemplateImpl(final FunElasticsearchProperties funElasticsearchProperties,
                                                    final FunLogHelper funLogHelper,
                                                    final List<IDocumentSerializer> documentSerializerList,
                                                    final List<IResponseDeserializer> responseDeserializerList) {
        super(funElasticsearchProperties, funLogHelper, documentSerializerList, responseDeserializerList);
    }

    /**
     * 通过配置创建一个 Elasticsearch 模板实例
     *
     * @param config Elasticsearch 配置
     * @return Elasticsearch 模板实例
     */
    @Override
    protected IElasticsearchTemplate createTemplate(final FunElasticsearchProperties.Config config) {
        return new ElasticsearchTemplateImpl(
            config, 
            funLogHelper,
            FunElasticsearch7AutoConfiguration.getJacksonJsonpMapper(),
            documentSerializerList, 
            responseDeserializerList);
    }
}
