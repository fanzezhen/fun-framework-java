package com.github.fanzezhen.fun.framework.data.elasticsearch.base.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.data.annotation.Column;
import com.github.fanzezhen.fun.framework.core.data.annotation.Entity;
import com.github.fanzezhen.fun.framework.core.data.enums.FunCoreDataExceptionEnum;
import com.github.fanzezhen.fun.framework.core.log.base.support.FunLogHelper;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.config.FunElasticsearchProperties;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IResponseDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.ISearchResult;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.serializer.IDocumentSerializer;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多数据源模板
 */
public abstract class BaseMultiDatasourceElasticsearchTemplate implements IElasticsearchTemplate {

    /**
     * IElasticsearchTemplate 实例配置
     */
    protected final FunElasticsearchProperties funElasticsearchProperties;
    protected final FunLogHelper funLogHelper;

    protected final List<IDocumentSerializer> documentSerializerList;

    protected final List<IResponseDeserializer> responseDeserializerList;
    /**
     * IElasticsearchTemplate 仓库
     * <p>
     * key :template name
     * value : template 实例
     */
    protected final ConcurrentHashMap<String, IElasticsearchTemplate> elasticsearchTemplateMap;

    protected BaseMultiDatasourceElasticsearchTemplate(FunElasticsearchProperties funElasticsearchProperties,
                                                       FunLogHelper funLogHelper,
                                                       List<IDocumentSerializer> documentSerializerList,
                                                       List<IResponseDeserializer> responseDeserializerList) {
        this.funElasticsearchProperties = funElasticsearchProperties;
        this.funLogHelper = funLogHelper;
        this.documentSerializerList = documentSerializerList;
        this.responseDeserializerList = responseDeserializerList;
        this.elasticsearchTemplateMap = new ConcurrentHashMap<>();
        if (funElasticsearchProperties != null && funElasticsearchProperties.getConfigs() != null) {
            for (FunElasticsearchProperties.Config config : funElasticsearchProperties.getConfigs()) {
                if (elasticsearchTemplateMap.containsKey(config.getName())) {
                    throw new ServiceException(FunCoreDataExceptionEnum.TEMPLATE_IMPL_NAME_DUPLICATED,
                        config.getName() + IElasticsearchTemplate.ELASTICSEARCH_MARK);
                }
                IElasticsearchTemplate elasticsearchTemplate = createTemplate(config);
                if (Objects.isNull(elasticsearchTemplate)) {
                    throw new ServiceException(FunCoreDataExceptionEnum.TEMPLATE_IMPL_CREATE_NULL,
                        config.getName() + IElasticsearchTemplate.ELASTICSEARCH_MARK);
                }
                elasticsearchTemplateMap.put(config.getName(), elasticsearchTemplate);
            }
        }
    }

    /**
     * 通过配置创建一个客户端
     *
     */
    protected abstract IElasticsearchTemplate createTemplate(FunElasticsearchProperties.Config config);

    private IElasticsearchTemplate findTemplate(Class<?> clz) {
        Entity document = AnnotationUtils.getAnnotation(clz, Entity.class);
        return findTemplate(document != null ? document.datasource() : null);
    }

    public IElasticsearchTemplate getDefaultOrAnyTemplate() {
        IElasticsearchTemplate elasticsearchTemplate =
            elasticsearchTemplateMap.get(funElasticsearchProperties.getDefaultDatasource());
        if (elasticsearchTemplate != null) {
            return elasticsearchTemplate;
        }
        if (!elasticsearchTemplateMap.isEmpty()) {
            return elasticsearchTemplateMap.values().iterator().next();
        }
        throw new ServiceException(FunCoreDataExceptionEnum.TEMPLATE_IMPL_CONFIG_NOT_EXISTS, "elasticsearch");
    }

    public IElasticsearchTemplate findTemplate(String datasource) {
        IElasticsearchTemplate elasticsearchTemplate;
        if (CharSequenceUtil.isEmpty(datasource)) {
            elasticsearchTemplate = elasticsearchTemplateMap.get(datasource);
        } else {
            elasticsearchTemplate = elasticsearchTemplateMap.get(funElasticsearchProperties.getDefaultDatasource());
        }
        if (elasticsearchTemplate != null) {
            return elasticsearchTemplate;
        }
        throw new ServiceException(FunCoreDataExceptionEnum.TEMPLATE_IMPL_NOT_EXISTS,
            datasource + IElasticsearchTemplate.ELASTICSEARCH_MARK);
    }


    /**
     * mGet方法，批量构建 get 请求
     * <p>
     * co.elastic.clients.elasticsearch.core.MGetRequest.Builder 作为入参
     *
     * @param ids 查询条件，索引名优先使用@Document中的
     * @param clz 文档类型
     *
     * @return 查询结果列表
     */
    @Override
    public <T> List<T> listByIds(Collection<? extends Serializable> ids, Class<T> clz) {
        final IElasticsearchTemplate template = findTemplate(clz);
        return template.listByIds(ids, clz);
    }

    /**
     * 根据唯一字段查询
     */
    @Override
    public <T> List<T> listByColumn(String column, Serializable value, Class<T> clz) {
        final IElasticsearchTemplate template = findTemplate(clz);
        return template.listByColumn(column, value, clz);
    }

    /**
     * 根据唯一字段查询
     */
    @Override
    public <T> List<T> listByColumn(String column, Collection<? extends Serializable> values, Class<T> clz) {
        final IElasticsearchTemplate template = findTemplate(clz);
        return template.listByColumn(column, values, clz);
    }

    /**
     * 根据唯一字段查询
     */
    @Override
    public <T> T get(String column, Serializable value, Class<T> clz) {
        final IElasticsearchTemplate template = findTemplate(clz);
        return template.get(column, value, clz);
    }
    /**
     * 根据 {@link Entity} dataSourceName 指定的数据源名称搜索
     *
     * @param requestBuilder 查询条件
     * @param clz     查询文档
     */
    @Override
    public <T> ISearchResult<T> search(Object requestBuilder, Class<T> clz) {
        final IElasticsearchTemplate template = findTemplate(clz);
        return template.search(requestBuilder, clz);
    }

    /**
     * esMsearch,对按照请求返回多个结果
     *
     * @param requestBuilders 多个不同的请求build co.elastic.clients.elasticsearch.core.SearchRequest.Builder
     * @param clz             文档类型
     *
     * @return 按照requestBuilders的顺序逐一包装的返回结果，如果某个request没有值，也会有一个空对象
     */
    @Override
    public <T> List<ISearchResult<T>> mSearch(Collection<?> requestBuilders, Class<T> clz) {
        final IElasticsearchTemplate template = findTemplate(clz);
        return template.mSearch(requestBuilders, clz);
    }

    @Override
    public <T> ISearchResult<T> scrollSearchByRequestBuilder(Object requestBuilder, Class<T> clz, Long timeSeconds) {
        final IElasticsearchTemplate template = findTemplate(clz);
        return template.scrollSearchByRequestBuilder(requestBuilder, clz, timeSeconds);
    }

    @Override
    public <T> ISearchResult<T> scrollSearchByScrollId(String scrollId, Class<T> clz, Long timeSeconds) {
        final IElasticsearchTemplate template = findTemplate(clz);
        return template.scrollSearchByScrollId(scrollId, clz, timeSeconds);
    }

    /**
     * 清除滚动搜索的scroll上下文
     *
     * @param clz       文档类型
     * @param scrollIds 需要清除的一个或多个scroll ID
     *
     * @return 如果清除操作成功则返回true，否则返回false
     */
    @Override
    public <T> boolean clearScroll(Class<T> clz, String scrollId, String... scrollIds) {
        final IElasticsearchTemplate template = findTemplate(clz);
        return template.clearScroll(clz, scrollId, scrollIds);
    }

    /**
     * 更新文档，不存在则创建，存在则更新
     *
     * @param document 文档对象
     *                 类需要被 {@link Entity} 标记
     *                 字段需要被 {@link Column} 标记出主键
     */
    @Override
    public String insert(IEntity<String> document) {
        return findTemplate(document.getClass()).insert(document);
    }

    /**
     * 查询多个文档
     * es6       org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * <p>
     * es7或es8  co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param request 查询条件
     * @param clz     文档类型
     *
     * @return 查询结果列表
     */
    @Override
    public <T> List<T> searchList(Object request, Class<T> clz) {
        return findTemplate(clz).searchList(request, clz);
    }

    /**
     * 查询单个文档
     * es6       org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * <p>
     * es7或es8  co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param request 查询条件
     * @param clz     文档类型
     *
     * @return 查询结果列表
     */
    @Override
    public <T> T searchOne(Object request, Class<T> clz) {
        return findTemplate(clz).searchOne(request, clz);
    }

    /**
     * 通过id查询文档
     *
     * @param id  es id
     * @param clz 文档类型
     *
     * @return es查询文档
     */
    @Override
    public <T> T getById(Serializable id, Class<T> clz) {
        return findTemplate(clz).getById(id, clz);
    }

    /**
     * 批量插入文档
     *
     * @param documents 文档
     *
     * @return 插入成功数量
     */
    @Override
    public boolean insert(Collection<IEntity<String>> documents ) {
        if (CollUtil.isEmpty(documents)) {
            return true;
        }
        final Class<?> aClass = documents.iterator().next().getClass();
        final IElasticsearchTemplate template = findTemplate(aClass);
        return template.insert(documents);
    }

    /**
     * 按条件删除索引数据
     *
     * @param ids 需要删除的id
     * @param clz 类需要被 {@link Entity} 标记
     *
     * @return 删除条数
     */
    @Override
    public boolean deleteById(Collection<String> ids, Class<? extends IEntity<String>> clz) {
        final IElasticsearchTemplate template = findTemplate(clz);
        return template.deleteById(ids, clz);
    }

}
