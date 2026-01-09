package com.github.fanzezhen.fun.framework.data.elasticsearch.base.model;

import cn.hutool.core.collection.CollUtil;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.Aggregation;
import com.github.fanzezhen.fun.framework.core.data.enums.FunCoreDataExceptionEnum;
import com.github.fanzezhen.fun.framework.core.data.template.ITemplate;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.model.result.PageResult;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHitsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.config.FunElasticsearchAutoConfiguration;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IResponseDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IElasticsearchResultDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
public abstract class BaseSearchResult<T, R> implements ISearchResult<T> {

    protected final List<IElasticsearchResultDeserializer> resultResolverList;

    protected final List<IResponseDeserializer> responseResolverList;

    protected final Class<T> tClass;

    protected BaseSearchResult(Class<T> tClass) {
        this.resultResolverList = FunElasticsearchAutoConfiguration.getStaticResultDeserializerList();
        this.responseResolverList = FunElasticsearchAutoConfiguration.getStaticResponseDeserializerList();
        this.tClass = tClass;
    }

    /**
     * 转换为list
     */
    @Override
    public List<T> asDocumentList() {
        return asList(tClass);
    }

    /**
     * 将搜索内容转化为list
     * <p>
     * 如果是聚合，需要在 tClass 上加 {@link Aggregation}
     */
    @Override
    public List<T> asList() {
        return asList(tClass);
    }

    /**
     * 将搜索内容转换为list
     * <p>
     *
     * @param vClass 搜索结果映射类
     *               <p>
     *               如果是聚合，需要在 vClass 上加 {@link Aggregation}
     */
    @Override
    public <V> List<V> asList(Class<V> vClass) {
        try {
            for (IElasticsearchResultDeserializer resultResolver : resultResolverList) {
                if (resultResolver.isSupport(getResponseAdapter(), vClass)) {
                    return resultResolver.deserialize(getResponseAdapter(), vClass);
                }
            }
        } catch (Exception e) {
            log.warn("elasticsearch数据结果解析失败：{}", ITemplate.getTable(vClass), e);
            throw new ServiceException(FunCoreDataExceptionEnum.DATA_RESULT_DESERIALIZE_FAILED, 
                "elasticsearch", e.getLocalizedMessage());
        }
        return Collections.emptyList();
    }

    /**
     * 将聚合内容转化为对象
     * <p>
     * 需要加 {@link Aggregation}
     *
     * @return 完整的聚合对象，对应es返回值中的 aggregations 字段
     */
    @Override
    public T asAggregations() {
        final List<T> list = this.asList();
        return CollUtil.isNotEmpty(list) ? list.get(0): null ;
    }

    /**
     * 转换为单个文档
     */
    @Override
    public T asDocument() {
        final List<T> list = asDocumentList();
        return CollUtil.isNotEmpty(list) ? list.get(0): null ;
    }

    /**
     * 转换为分页对象
     */
    @Override
    public PageResult<T> asPageResult(Long currentPage, Long pageSize) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setCurrentPage(currentPage);
        pageResult.setPageSize(pageSize);
        pageResult.setTotalTime(this.getTotalTime());
        pageResult.setTotal(this.getTotalHits());
        pageResult.setRowList(this.asDocumentList());
        return pageResult;
    }

    public static <T> ISearchResult<T> empty(Class<T> clz) {
        return new BaseSearchResult<>(clz) {
            /**
             * 总数量
             */
            @Override
            public long getTotalHits() {
                return 0;
            }

            /**
             * 总耗时
             */
            @Override
            public double getTotalTime() {
                return 0;
            }

            /**
             * 获取游标id
             */
            @Override
            public String getScrollId() {
                return null;
            }

            @Override
            public IResponseAdapter getResponseAdapter() {
                return new IResponseAdapter() {
                    /**
                     * 获取聚合
                     */
                    @Override
                    public IAggregationsAdapter getAggregationsAdapter() {
                        return null;
                    }

                    /**
                     * 获取hits
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
