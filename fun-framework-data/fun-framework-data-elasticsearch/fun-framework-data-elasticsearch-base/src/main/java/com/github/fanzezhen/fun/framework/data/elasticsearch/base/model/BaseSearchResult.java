package com.github.fanzezhen.fun.framework.data.elasticsearch.base.model;

import cn.hutool.core.collection.CollUtil;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.Aggregation;
import com.github.fanzezhen.fun.framework.core.model.enums.FunCoreDataExceptionEnum;
import com.github.fanzezhen.fun.framework.core.model.template.ITemplate;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.model.dto.PageDTO;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHitsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.config.FunElasticsearchAutoConfiguration;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IResponseDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IElasticsearchResultDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * Elasticsearch 查询结果抽象基类
 * <p>
 * 提供查询结果的统一封装和转换功能，支持将 ES 响应转换为文档列表、聚合对象、分页对象等多种格式。
 * </p>
 * <p>主要功能：</p>
 * <ul>
 *   <li>将 ES 响应转换为 Java 对象列表</li>
 *   <li>支持聚合结果的反序列化（需配合 @Aggregation 注解）</li>
 *   <li>提供分页结果封装</li>
 *   <li>支持自定义反序列化器</li>
 * </ul>
 *
 * @param <T> 文档类型
 * @param <R> 响应类型
 */
@Slf4j
public abstract class BaseSearchResult<T, R> implements ISearchResult<T> {

    /**
     * 结果反序列化器列表
     */
    protected final List<IElasticsearchResultDeserializer> resultResolverList;

    /**
     * 响应反序列化器列表
     */
    protected final List<IResponseDeserializer> responseResolverList;

    /**
     * 文档类型
     */
    protected final Class<T> documentClass;

    /**
     * 构造方法
     *
     * @param documentClass 文档类型
     */
    protected BaseSearchResult(final Class<T> documentClass) {
        this.resultResolverList = FunElasticsearchAutoConfiguration.getStaticResultDeserializerList();
        this.responseResolverList = FunElasticsearchAutoConfiguration.getStaticResponseDeserializerList();
        this.documentClass = documentClass;
    }

    /**
     * 转换为文档列表
     * <p>
     * 使用默认的文档类型进行转换。
     * </p>
     *
     * @return 文档列表
     */
    @Override
    public List<T> asDocumentList() {
        return asList(documentClass);
    }

    /**
     * 将搜索内容转化为列表
     * <p>
     * 使用默认的文档类型进行转换。
     * 如果是聚合结果，需要在类上添加 {@link Aggregation} 注解。
     * </p>
     *
     * @return 结果列表
     */
    @Override
    public List<T> asList() {
        return asList(documentClass);
    }

    /**
     * 将搜索内容转换为指定类型的列表
     * <p>
     * 使用注册的反序列化器将 ES 响应转换为 Java 对象列表。
     * 如果是聚合结果，需要在 vClass 上添加 {@link Aggregation} 注解。
     * </p>
     *
     * @param vClass 搜索结果映射类
     * @param <V> 结果类型
     * @return 结果列表
     * @throws ServiceException 如果反序列化失败
     */
    @Override
    public <V> List<V> asList(final Class<V> vClass) {
        try {
            for (final IElasticsearchResultDeserializer resultResolver : resultResolverList) {
                if (resultResolver.isSupport(getResponseAdapter(), vClass)) {
                    return resultResolver.deserialize(getResponseAdapter(), vClass);
                }
            }
            return Collections.emptyList();
        } catch (final Exception e) {
            log.warn("elasticsearch数据结果解析失败：{}", ITemplate.getTable(vClass), e);
            throw new ServiceException(FunCoreDataExceptionEnum.DATA_RESULT_DESERIALIZE_FAILED,
                "elasticsearch", e.getLocalizedMessage());
        }
    }

    /**
     * 将聚合内容转化为对象
     * <p>
     * 返回第一个聚合结果对象。
     * 类上需要添加 {@link Aggregation} 注解。
     * </p>
     *
     * @return 聚合对象，对应 ES 返回值中的 aggregations 字段；如果没有聚合结果则返回 null
     */
    @Override
    public T asAggregations() {
        final List<T> list = this.asList();
        return CollUtil.isNotEmpty(list) ? list.getFirst() : null;
    }

    /**
     * 转换为单个文档
     * <p>
     * 返回第一个命中的文档。
     * </p>
     *
     * @return 文档对象；如果没有命中则返回 null
     */
    @Override
    public T asDocument() {
        final List<T> list = asDocumentList();
        return CollUtil.isNotEmpty(list) ? list.getFirst() : null;
    }

    /**
     * 转换为分页对象
     * <p>
     * 将查询结果封装为分页对象，包含当前页码、每页大小、总数、总耗时和记录列表。
     * </p>
     *
     * @param currentPage 当前页码
     * @param pageSize 每页大小
     * @return 分页结果对象
     */
    @Override
    public PageDTO<T> asPageResult(final int currentPage, final int pageSize) {
        final PageDTO<T> pageDTO = new PageDTO<>();
        pageDTO.setCurrent(currentPage);
        pageDTO.setSize(pageSize);
        pageDTO.setTotalTime(this.getTotalTime());
        pageDTO.setTotal(this.getTotalHits());
        pageDTO.setRecords(this.asDocumentList());
        return pageDTO;
    }

    /**
     * 创建空的查询结果对象
     * <p>
     * 用于在查询无结果或出错时返回空结果，避免返回 null。
     * </p>
     *
     * @param clz 文档类型
     * @param <T> 文档类型
     * @return 空的查询结果对象
     */
    public static <T> ISearchResult<T> empty(final Class<T> clz) {
        return new BaseSearchResult<>(clz) {
            /**
             * 获取总命中数
             *
             * @return 总是返回 0
             */
            @Override
            public long getTotalHits() {
                return 0;
            }

            /**
             * 获取查询耗时
             *
             * @return 总是返回 0
             */
            @Override
            public double getTotalTime() {
                return 0;
            }

            /**
             * 获取游标 ID
             *
             * @return 总是返回 null
             */
            @Override
            public String getScrollId() {
                return null;
            }

            /**
             * 获取响应适配器
             *
             * @return 空的响应适配器
             */
            @Override
            public IResponseAdapter getResponseAdapter() {
                return new IResponseAdapter() {
                    /**
                     * 获取聚合适配器
                     *
                     * @return 总是返回 null
                     */
                    @Override
                    public IAggregationsAdapter getAggregationsAdapter() {
                        return null;
                    }

                    /**
                     * 获取命中适配器
                     *
                     * @return 总是返回 null
                     */
                    @Override
                    public IHitsAdapter getHitsAdapter() {
                        return null;
                    }
                };
            }
        };
    }
}
