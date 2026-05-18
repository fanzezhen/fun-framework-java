package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.db.sql.Direction;
import cn.hutool.db.sql.Order;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.CardinalityAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.ScriptedMetricAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.ScriptedMetricAggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.ClearScrollRequest;
import co.elastic.clients.elasticsearch.core.ClearScrollResponse;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.MgetRequest;
import co.elastic.clients.elasticsearch.core.MgetResponse;
import co.elastic.clients.elasticsearch.core.MsearchRequest;
import co.elastic.clients.elasticsearch.core.MsearchResponse;
import co.elastic.clients.elasticsearch.core.ScrollRequest;
import co.elastic.clients.elasticsearch.core.ScrollResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.msearch.RequestItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransportBase;
import co.elastic.clients.transport.rest_client.RestClientOptions;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.util.NamedValue;
import co.elastic.clients.util.ObjectBuilder;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.model.annotation.Column;
import com.github.fanzezhen.fun.framework.core.model.annotation.Entity;
import com.github.fanzezhen.fun.framework.core.model.common.NestedAggregationCondition;
import com.github.fanzezhen.fun.framework.core.model.common.SumAggregationCondition;
import com.github.fanzezhen.fun.framework.core.model.template.ITemplate;
import com.github.fanzezhen.fun.framework.core.log.support.FunLogHelper;
import com.github.fanzezhen.fun.framework.core.model.common.AggregationCondition;
import com.github.fanzezhen.fun.framework.core.model.bucket.CountBucket;
import com.github.fanzezhen.fun.framework.core.model.bucket.SumBucket;
import com.github.fanzezhen.fun.framework.core.model.constant.NormalTypeConstant;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.config.FunElasticsearchAutoConfiguration;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.config.FunElasticsearchProperties;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.ElasticsearchKeywordConstants;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.ElasticsearchScriptConstants;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IElasticsearchResultDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IResponseDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.bucket.CountBucketsAggregation;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.DocumentData;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.bucket.HitsCountBucket;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.ISearchResult;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.bucket.SumBucketsAggregation;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.serializer.IDocumentSerializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.template.BaseElasticsearchTemplate;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model.JsonResponseAdapterV7;
import jakarta.json.JsonObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Elasticsearch 7.x 模板实现类
 *
 * <p>提供完整的 Elasticsearch 7.x 操作功能，包括查询、聚合、滚动查询、批量操作等。
 * 支持多种查询方式和聚合类型，自动处理响应结果的转换和序列化。
 */
@Slf4j
public class ElasticsearchTemplateImpl extends BaseElasticsearchTemplate {

    /**
     * Jackson JSON 映射器
     */
    private final JacksonJsonpMapper jacksonJsonpMapper;

    /**
     * Elasticsearch 客户端实例
     */
    private ElasticsearchClient elasticsearchClient;

    /**
     * 构造函数
     *
     * @param config Elasticsearch 配置
     * @param funLogHelper 日志辅助类
     * @param jacksonJsonpMapper Jackson JSON 映射器
     * @param documentSerializerList 文档序列化器列表
     * @param responseDeserializerList 响应反序列化器列表
     */
    public ElasticsearchTemplateImpl(final FunElasticsearchProperties.Config config,
                                     final FunLogHelper funLogHelper,
                                     final JacksonJsonpMapper jacksonJsonpMapper,
                                     final List<IDocumentSerializer> documentSerializerList,
                                     final List<IResponseDeserializer> responseDeserializerList) {
        super(config, funLogHelper, documentSerializerList, responseDeserializerList);
        this.jacksonJsonpMapper = jacksonJsonpMapper;
        initElasticsearchClient();
    }

    /**
     * 初始化 Elasticsearch 客户端
     *
     * <p>根据配置创建 RestClient 和 ElasticsearchClient 实例，
     * 配置认证信息、超时参数和 Content-Type 头。
     */
    @SuppressWarnings("unchecked")
    private void initElasticsearchClient() {
        // ========== 原有逻辑（uri、认证、RestClientBuilder）保持不变 ==========
        final List<String> uris = config.getUris();
        HttpHost[] httpHostArray = new HttpHost[uris.size()];
        try {
            for (int i = 0; i < uris.size(); i++) {
                final String uri = uris.get(i);
                httpHostArray[i] = new HttpHost(createHttpHost(new URI(uri)));
            }
        } catch (URISyntaxException e) {
            throw new ServiceException("es uri错误 " + uris, e);
        }

        CredentialsProvider credentialsProvider;
        if (CharSequenceUtil.isNotEmpty(config.getUsername()) && CharSequenceUtil.isNotBlank(config.getPassword())) {
            credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(config.getUsername(), config.getPassword()));
        } else {
            credentialsProvider = null;
        }

        RestClientBuilder builder = RestClient
            .builder(httpHostArray)
            .setRequestConfigCallback(requestConfigBuilder -> {
                Optional.ofNullable(config.getSocketTimeout()).ifPresent(e -> requestConfigBuilder.setSocketTimeout((int) e.toMillis()));
                Optional.ofNullable(config.getConnectTimeout()).ifPresent(e -> requestConfigBuilder.setConnectTimeout((int) e.toMillis()));
                Optional.ofNullable(config.getConnectionRequestTimeout()).ifPresent(e -> requestConfigBuilder.setConnectionRequestTimeout((int) e.toMillis()));
                return requestConfigBuilder;
            })
            .setHttpClientConfigCallback(httpAsyncClientBuilder -> {
                if (Objects.nonNull(credentialsProvider)) {
                    httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
                if (Objects.nonNull(config.getKeepAliveTime())) {
                    httpAsyncClientBuilder.setConnectionTimeToLive(config.getKeepAliveTime().getSeconds(), TimeUnit.SECONDS);
                }
                return httpAsyncClientBuilder;
            });

        RestClient restClient = builder.build();
        // 1. 从默认 RequestOptions 构建 Builder，覆盖 Content-Type
        RequestOptions.Builder requestOptionsBuilder = RequestOptions.DEFAULT.toBuilder();
        // 设置为标准 application/json(使用 Apache HttpClient 常量)
        requestOptionsBuilder.removeHeader(HttpHeaders.CONTENT_TYPE);
        requestOptionsBuilder.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        // 可选：设置 Accept 头
        requestOptionsBuilder.removeHeader(HttpHeaders.ACCEPT);
        requestOptionsBuilder.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        // 构建最终的 RequestOptions
        RequestOptions requestOptions = requestOptionsBuilder.build();
        // 2. 直接通过公开构造器创建 RestClientOptions
        RestClientOptions restClientOptions = new RestClientOptions(requestOptions);
        // 3. 创建 Transport 时传入自定义的 RestClientOptions
        RestClientTransport transport = new RestClientTransport(
            restClient,
            jacksonJsonpMapper,
            restClientOptions // 传入自定义配置
        );
        try {
            Field field = ReflectUtil.getField(ElasticsearchTransportBase.class, "endpointsMissingProductHeader");
            Object endpointsMissingProductHeader = ReflectUtil.getStaticFieldValue(field);
            if (endpointsMissingProductHeader instanceof Set) {
                // 一次性添加常用端点（覆盖绝大多数业务场景）
                ((Set<String>) endpointsMissingProductHeader).addAll(Arrays.asList(
                    "es/search",        // 单索引搜索
                    "es/msearch",       // 多索引搜索
                    "es/get",           // 获取文档
                    "es/mget",          // 多索引获取文档
                    "es/scroll",        // 滚动查询
                    "es/clear_scroll",  // 清除游标
                    "es/index",         // 新增/更新文档
                    "es/delete",        // 删除文档
                    "es/bulk",          // 批量操作
                    "es/count"          // 计数
                ));
            }
        } catch (Exception e) {
            throw new ServiceException("添加 ES 校验白名单失败", e);
        }
        // ========== 原有逻辑保持不变 ==========
        this.elasticsearchClient = new ElasticsearchClient(transport);
    }

    /**
     * 高级查询，查询条件与 searchList 类似，但可以通过 {@link ISearchResult} 获取其他结果
     *
     * <p>ES6 使用 org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * <p>ES7 或 ES8 使用 co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param requestBuilder 查询条件构建器
     * @param clz 文档类型
     * @param indexName 索引名称
     * @param <T> 文档泛型类型
     * @return SearchResult 对象
     */
    @Override
    public <T> ISearchResult<T> search(final Object requestBuilder, final Class<T> clz, final String indexName) {
        if (!(requestBuilder instanceof SearchRequest.Builder searchRequestBuilder)) {
            throw new ElasticsearchException("request 入参必须为 co.elastic.clients.elasticsearch.core.SearchRequest.Builder 类型");
        }
        SearchRequest searchRequest = SearchRequest.of(builder -> searchRequestBuilder.index(indexName));
        final SearchResponse<JSONObject> response = executeByLog(
            searchRequest,
            request -> elasticsearchClient.search(request, JSONObject.class)
        );
        return convertResponseToResult(response, clz);
    }

    /**
     * 批量查询，按照请求顺序返回多个结果
     *
     * @param requestBuilders 多个不同的请求构建器集合（co.elastic.clients.elasticsearch.core.SearchRequest.Builder）
     * @param clz 文档类型
     * @param <T> 文档泛型类型
     * @return 按照 requestBuilders 的顺序逐一包装的返回结果，如果某个 request 没有值，也会有一个空对象
     */
    @Override
    public <T> List<ISearchResult<T>> mSearch(final Collection<?> requestBuilders, final Class<T> clz) {
        if (null == requestBuilders || requestBuilders.isEmpty()) {
            return new ArrayList<>(0);
        }
        String indexName = getIndexName(clz);
        List<RequestItem> requestItemList = requestBuilders.stream().map(o -> {
            if (o instanceof SearchRequest.Builder searchRequestBuilder) {
                SearchRequest searchRequest = searchRequestBuilder.build();
                return RequestItem.of(itemBuilder -> itemBuilder
                    // 设置 RequestItem 的 header（元信息）
                    .header(headerBuilder -> {
                        headerBuilder.index(indexName);
                        // 继承 SearchRequest 中的索引名
                        if (searchRequest.index() != null && !searchRequest.index().isEmpty()) {
                            headerBuilder.index(searchRequest.index());
                        }
                        // 可选：继承其他元信息（如 routing、ignoreUnavailable 等）
                        if (searchRequest.routing() != null) {
                            headerBuilder.routing(searchRequest.routing());
                        }
                        return headerBuilder;
                    })
                    // 设置 RequestItem 的 body（查询条件）
                    .body(bodyBuilder -> {
                        // 继承 SearchRequest 的所有查询配置（query、size、sort、from 等）
                        if (searchRequest.query() != null) {
                            bodyBuilder.query(searchRequest.query());
                        }
                        if (searchRequest.size() != null) {
                            bodyBuilder.size(searchRequest.size());
                        }
                        if (searchRequest.sort() != null && !searchRequest.sort().isEmpty()) {
                            bodyBuilder.sort(searchRequest.sort());
                        }
                        if (searchRequest.from() != null) {
                            bodyBuilder.from(searchRequest.from());
                        }
                        if (searchRequest.aggregations() != null) {
                            bodyBuilder.aggregations(searchRequest.aggregations());
                        }
                        // 可扩展：继承其他查询参数（如 aggs、sourceFilter 等）
                        return bodyBuilder;
                    })
                );
            }
            return null;
        }).toList();
        MsearchRequest msearchRequest = MsearchRequest.of(builder -> builder.searches(requestItemList));
        MsearchResponse<JSONObject> response = executeByLog(
            msearchRequest,
            request -> elasticsearchClient.msearch(request, JSONObject.class));
        return response.responses().stream()
            .map(multiSearchResponseItem ->
                convertResponseToResult(multiSearchResponseItem, clz))
            .toList();
    }

    /**
     * 查询分组聚合近似结果
     *
     * <p>使用 terms 聚合进行分组统计，结果为近似值。
     *
     * @param requestBuilder 查询条件构建器
     * @param clz 文档类型
     * @param aggregationCondition 聚合条件
     * @param <T> 文档泛型类型
     * @return 分组统计桶列表
     */
    @Override
    public <T> List<CountBucket> searchTermsAggregationBucketList(final Object requestBuilder,
                                                                  final Class<T> clz,
                                                                  final AggregationCondition aggregationCondition) {
        if (!(requestBuilder instanceof SearchRequest.Builder searchRequestBuilder)) {
            throw new ElasticsearchException("request 入参必须为 co.elastic.clients.elasticsearch.core.SearchRequest.Builder 类型");
        }
        String key = ElasticsearchKeywordConstants.PREFIX_GROUP_COUNT + aggregationCondition.getFieldName();
        searchRequestBuilder.aggregations(key, agg -> aggregationsContainerBuilder(agg, aggregationCondition));
        searchRequestBuilder.size(0);
        ISearchResult<CountBucketsAggregation> searchResult = search(searchRequestBuilder, CountBucketsAggregation.class, getIndexName(clz));
        CountBucketsAggregation aggregations = searchResult.asAggregations();
        return aggregations != null ? aggregations.getBucketList() : null;
    }

    /**
     * 查询分组聚合结果（精确值）
     *
     * <p>使用脚本化度量聚合进行分组统计，占用的空间和查询条件过滤后的 key 数量成正比，
     * 不适合 key 数量过多的场景。
     *
     * @param requestBuilder 查询条件构建器
     * @param clz 文档类型
     * @param aggregationCondition 聚合条件
     * @param <T> 文档泛型类型
     * @return 分组统计桶列表
     */
    @Override
    public <T> List<CountBucket> searchScriptedMetricAggregationCountBucketList(final Object requestBuilder,
                                                                                final Class<T> clz,
                                                                                final AggregationCondition aggregationCondition) {
        if (!(requestBuilder instanceof SearchRequest.Builder searchRequestBuilder)) {
            throw new ElasticsearchException("request 入参必须为 co.elastic.clients.elasticsearch.core.SearchRequest.Builder 类型");
        }
        String key = ElasticsearchKeywordConstants.PREFIX_GROUP_COUNT + aggregationCondition.getFieldName();
        Function<Aggregation.Builder, ObjectBuilder<Aggregation>> builderScriptedMetricBuilderFunction =
            buildScriptedMetricAggregation(aggregationCondition);
        searchRequestBuilder.aggregations(key, builderScriptedMetricBuilderFunction);
        searchRequestBuilder.size(0);
        ISearchResult<CountBucketsAggregation> searchResult = search(searchRequestBuilder, CountBucketsAggregation.class, getIndexName(clz));
        CountBucketsAggregation aggregations = searchResult.asAggregations();
        return aggregations != null ? aggregations.getBucketList() : null;
    }

    /**
     * 查询分组求和聚合结果（精确值）
     *
     * <p>使用脚本化度量聚合进行分组求和统计。
     *
     * @param requestBuilder 查询条件构建器
     * @param clz 文档类型
     * @param aggregationCondition 求和聚合条件
     * @param <T> 文档泛型类型
     * @return 分组求和桶列表
     */
    @Override
    public <T> List<SumBucket> searchScriptedMetricAggregationSumBucketList(final Object requestBuilder,
                                                                             final Class<T> clz,
                                                                             final SumAggregationCondition aggregationCondition) {
        if (!(requestBuilder instanceof SearchRequest.Builder searchRequestBuilder)) {
            throw new ElasticsearchException("request 入参必须为 co.elastic.clients.elasticsearch.core.SearchRequest.Builder 类型");
        }
        String key = ElasticsearchKeywordConstants.PREFIX_GROUP_COUNT + aggregationCondition.getFieldName();
        Function<Aggregation.Builder, ObjectBuilder<Aggregation>> builderScriptedMetricBuilderFunction =
            buildScriptedMetricAggregation(aggregationCondition);
        searchRequestBuilder.aggregations(key, builderScriptedMetricBuilderFunction);
        searchRequestBuilder.size(0);
        ISearchResult<SumBucketsAggregation> searchResult = search(searchRequestBuilder, SumBucketsAggregation.class, getIndexName(clz));
        SumBucketsAggregation aggregations = searchResult.asAggregations();
        return aggregations != null ? aggregations.getBucketList() : null;
    }

    /**
     * 查询分组聚合近似结果（包含命中文档）
     *
     * <p>使用 terms 聚合进行分组统计，并返回每个分组的 top hits 文档。
     *
     * @param requestBuilder 查询条件构建器
     * @param clz 文档类型
     * @param aggregationCondition 嵌套聚合条件
     * @param <T> 文档泛型类型
     * @return 包含命中文档的分组统计桶列表
     */
    @Override
    public <T> List<HitsCountBucket<T>> searchTermsAggregationHitsBucketList(final Object requestBuilder,
                                                                             final Class<T> clz,
                                                                             final NestedAggregationCondition aggregationCondition) {
        if (!(requestBuilder instanceof SearchRequest.Builder searchRequestBuilder)) {
            throw new ElasticsearchException("request 入参必须为 co.elastic.clients.elasticsearch.core.SearchRequest.Builder 类型");
        }
        String key = ElasticsearchKeywordConstants.PREFIX_GROUP_COUNT + aggregationCondition.getFieldName();
        searchRequestBuilder.aggregations(key, agg ->
            aggregationsContainerBuilder(agg, aggregationCondition)
                .aggregations(NormalTypeConstant.STR_RECORDS, child ->
                    child.topHits(topHitsBuilder -> {
                        if (aggregationCondition.getHitsLimit() != null) {
                            topHitsBuilder.size(aggregationCondition.getHitsLimit());
                        }
                        if (aggregationCondition.getHitsOrder() != null) {
                            co.elastic.clients.elasticsearch._types.SortOrder hitsSortOrder =
                                Direction.DESC.equals(aggregationCondition.getHitsOrder().getDirection()) ?
                                    co.elastic.clients.elasticsearch._types.SortOrder.Desc :
                                    co.elastic.clients.elasticsearch._types.SortOrder.Asc;
                            topHitsBuilder.sort(sortBuilder -> sortBuilder.field(b -> b
                                .field(aggregationCondition.getHitsOrder().getField())
                                .order(hitsSortOrder)));
                        }
                        if (aggregationCondition.needFilterHitSource()) {
                            topHitsBuilder.source(sourceBuilder -> sourceBuilder.filter(builder -> {
                                if (aggregationCondition.getHitSourceIncludes() != null) {
                                    if (aggregationCondition.getHitSourceIncludes() instanceof List<String> includes) {
                                        builder.includes(includes);
                                    } else {
                                        builder.includes(new ArrayList<>(aggregationCondition.getHitSourceIncludes()));
                                    }
                                }
                                if (aggregationCondition.getHitSourceExcludes() != null) {
                                    if (aggregationCondition.getHitSourceExcludes() instanceof List<String> excludes) {
                                        builder.excludes(excludes);
                                    } else {
                                        builder.includes(new ArrayList<>(aggregationCondition.getHitSourceExcludes()));
                                    }
                                }
                                return builder;
                            }));
                        }
                        return topHitsBuilder;
                    })));
        searchRequestBuilder.size(0);
        SearchRequest searchRequest = SearchRequest.of(builder -> searchRequestBuilder.index(getIndexName(clz)));
        final SearchResponse<JSONObject> response = executeByLog(
            searchRequest,
            request -> elasticsearchClient.search(request, JSONObject.class)
        );
        return response.aggregations().get(key).sterms().buckets().array().stream().map(stringTermsBucket -> {
            HitsCountBucket<T> hitsBucket = new HitsCountBucket<>(stringTermsBucket.key().stringValue(), stringTermsBucket.docCount());
            JSONArray hits = new JSONArray(aggregationCondition.getHitsLimit());
            for (Hit<JsonData> hit : stringTermsBucket.aggregations().get(NormalTypeConstant.STR_RECORDS).topHits().hits().hits()) {
                JsonData source = hit.source();
                hits.add(new JSONObject().fluentPut("_source", source != null ? source.toJson().asJsonObject() : MapUtil.empty()).fluentPut("_id", hit.id()));
            }
            hitsBucket.setHitList(deserializer(hits, clz));
            return hitsBucket;
        }).toList();
    }

    /**
     * 构建聚合容器
     *
     * @param agg 聚合构建器
     * @param aggregationCondition 聚合条件
     * @return 聚合容器构建器
     */
    @SuppressWarnings("unchecked")
    private static Aggregation.Builder.ContainerBuilder aggregationsContainerBuilder(
        final Aggregation.Builder agg,
        final AggregationCondition aggregationCondition) {
        co.elastic.clients.elasticsearch._types.SortOrder sortOrder = getSortOrder(aggregationCondition.getSortOrder());
        return agg
            .terms(b -> {
                TermsAggregation.Builder builder = b.field(aggregationCondition.getFieldName());
                if (sortOrder != null) {
                    builder.order(NamedValue.of(NormalTypeConstant.STR_UNDERLINE_COUNT, sortOrder));
                }
                if (aggregationCondition.getLimit() != null) {
                    builder.size(aggregationCondition.getLimit());
                }
                return builder;
            });
    }

    /**
     * 反序列化命中文档列表
     *
     * @param hits 命中文档列表
     * @param clz 文档类型
     * @param <T> 文档泛型类型
     * @return 反序列化后的文档列表
     */
    private static <T> List<T> deserializer(final List<?> hits, final Class<T> clz) {
        JsonResponseAdapterV7 hitsResponseAdapter = new JsonResponseAdapterV7(new JSONObject().fluentPut("hits", new JSONObject().fluentPut("hits", hits)));
        List<T> list = Collections.emptyList();
        for (IElasticsearchResultDeserializer deserializer :
            FunElasticsearchAutoConfiguration.getStaticResultDeserializerList()) {
            if (deserializer.isSupport(hitsResponseAdapter, clz)) {
                list = deserializer.deserialize(hitsResponseAdapter, clz);
                break;
            }
        }
        return list;
    }

    /**
     * 获取排序顺序
     *
     * @param aggregationCondition 排序条件
     * @return Elasticsearch 排序顺序
     */
    private static co.elastic.clients.elasticsearch._types.SortOrder getSortOrder(final SortOrder aggregationCondition) {
        co.elastic.clients.elasticsearch._types.SortOrder sortOrder;
        switch (aggregationCondition) {
            case DESCENDING -> sortOrder = co.elastic.clients.elasticsearch._types.SortOrder.Desc;
            case ASCENDING -> sortOrder = co.elastic.clients.elasticsearch._types.SortOrder.Asc;
            case null, default -> sortOrder = null;
        }
        return sortOrder;
    }

    /**
     * 查询分组聚合精确结果（包含命中文档）
     *
     * <p>使用脚本化度量聚合进行分组统计，并返回每个分组的命中文档。
     * 占用的空间和查询条件过滤后的 key 数量成正比，不适合 key 数量过多的场景。
     *
     * @param requestBuilder 查询条件构建器
     * @param clz 文档类型
     * @param aggregationCondition 嵌套聚合条件
     * @param <T> 文档泛型类型
     * @return 包含命中文档的分组统计桶列表
     */
    @Override
    public <T> List<HitsCountBucket<T>> searchScriptedMetricAggregationHitsBucketList(final Object requestBuilder,
                                                                                      final Class<T> clz,
                                                                                      final NestedAggregationCondition aggregationCondition) {
        if (!(requestBuilder instanceof SearchRequest.Builder searchRequestBuilder)) {
            throw new ElasticsearchException("request 入参必须为 co.elastic.clients.elasticsearch.core.SearchRequest.Builder 类型");
        }
        String fieldName = aggregationCondition.getFieldName();
        Integer limit = aggregationCondition.getLimit();
        Integer hitsLimit = aggregationCondition.getHitsLimit();
        Order hitsOrder = aggregationCondition.getHitsOrder();
        String mapScriptDoc = ElasticsearchScriptConstants.MAP_SCRIPT_DEFAULT_DOC;
        if (aggregationCondition.needFilterHitSource()) {
            if (CollUtil.isNotEmpty(aggregationCondition.getHitSourceIncludes())) {
                mapScriptDoc = aggregationCondition.getHitSourceIncludes().stream()
                    .map(s -> String.format(ElasticsearchScriptConstants.MAP_SCRIPT_DOC_ITEM_TEMPLATE, s, s))
                    .collect(Collectors.joining());
            }
            if (CollUtil.isNotEmpty(aggregationCondition.getHitSourceExcludes())) {
                mapScriptDoc += aggregationCondition.getHitSourceExcludes().stream()
                    .map(s -> "      docData.remove('" + s + "');\n").collect(Collectors.joining());
            }
        }
        String mapScriptHitSortScript = CharSequenceUtil.EMPTY;
        String hitSortScript = CharSequenceUtil.EMPTY;
        if (hitsOrder != null) {
            if (Direction.DESC.equals(hitsOrder.getDirection())) {
                hitSortScript = String.format(ElasticsearchScriptConstants.SCRIPT_HIT_SORT_TEMPLATE, hitsOrder.getField(),
                    hitsOrder.getField(), ElasticsearchScriptConstants.OBJECT_SORT_DESC_SCRIPT, hitsLimit);
            } else if (Direction.ASC.equals(hitsOrder.getDirection())) {
                hitSortScript = String.format(ElasticsearchScriptConstants.SCRIPT_HIT_SORT_TEMPLATE, hitsOrder.getField(),
                    hitsOrder.getField(), ElasticsearchScriptConstants.OBJECT_SORT_ASC_SCRIPT, hitsLimit);
            }
            mapScriptHitSortScript = String.format(ElasticsearchScriptConstants.MAP_SCRIPT_HIT_SORT_TEMPLATE, hitSortScript);
        }
        String mapScript = String.format(ElasticsearchScriptConstants.MAP_SCRIPT_COUNT_DOC_TEMPLATE, fieldName, fieldName,
            mapScriptDoc, hitsLimit, mapScriptHitSortScript);
        String combineScript = "return state.groupMap;";
        String reduceScriptSort = CharSequenceUtil.EMPTY;
        if (SortOrder.DESCENDING.equals(aggregationCondition.getSortOrder())) {
            reduceScriptSort = ElasticsearchScriptConstants.REDUCE_SCRIPT_SORT_DESC_TEMPLATE;
        } else if (SortOrder.ASCENDING.equals(aggregationCondition.getSortOrder())) {
            reduceScriptSort = ElasticsearchScriptConstants.REDUCE_SCRIPT_SORT_ASC_TEMPLATE;
        }
        String reduceScriptLimit = CharSequenceUtil.EMPTY;
        if (limit != null) {
            reduceScriptLimit = String.format(ElasticsearchScriptConstants.REDUCE_SCRIPT_LIMIT_TEMPLATE, limit, limit);
        }
        String reduceScript = String.format(ElasticsearchScriptConstants.REDUCE_SCRIPT_TEMPLATE, hitsLimit, hitSortScript,
            hitsLimit, hitsLimit, reduceScriptSort, reduceScriptLimit);
        String key = ElasticsearchKeywordConstants.PREFIX_GROUP_COUNT + fieldName;
        searchRequestBuilder.aggregations(key, agg -> agg
            .scriptedMetric(scriptedMetricBuilder -> {
                scriptedMetricBuilder
                    .initScript(s -> s.inline(in -> in.source(ElasticsearchScriptConstants.INIT_SCRIPT).lang(ElasticsearchScriptConstants.PAINLESS)))
                    .mapScript(s -> s.inline(in -> in.source(mapScript).lang(ElasticsearchScriptConstants.PAINLESS)))
                    .combineScript(s -> s.inline(in -> in.source(combineScript).lang(ElasticsearchScriptConstants.PAINLESS)))
                    .reduceScript(s -> s.inline(in -> in.source(reduceScript).lang(ElasticsearchScriptConstants.PAINLESS)));
                return scriptedMetricBuilder;
            }));
        searchRequestBuilder.size(0);
        SearchRequest searchRequest = SearchRequest.of(builder -> searchRequestBuilder.index(getIndexName(clz)));
        final SearchResponse<JSONObject> response = executeByLog(
            searchRequest,
            request -> elasticsearchClient.search(request, JSONObject.class)
        );
        return response.aggregations().get(key).scriptedMetric().value().toJson().asJsonArray().stream().map(jsonValue -> {
            JsonObject jsonValueObject = jsonValue.asJsonObject();
            HitsCountBucket<T> hitsBucket = new HitsCountBucket<>(jsonValueObject.getString("key"), (long) jsonValueObject.getInt("doc_count"));
            List<JSONObject> hitList = jsonValueObject.getJsonArray("hits").stream().map(o -> {
                JsonObject jsonObject = o.asJsonObject();
                return new JSONObject().fluentPut("_id", jsonObject.getString("id")).fluentPut("_source", jsonObject);
            }).toList();
            hitsBucket.setHitList(deserializer(hitList, clz));
            return hitsBucket;
        }).toList();
    }

    /**
     * 计算不重复值的数量（近似值）
     *
     * <p>使用 cardinality 聚合计算字段的去重数量，结果为近似值。
     *
     * @param requestBuilder 查询条件构建器
     * @param column 列选择函数
     * @param clz 文档类型
     * @param <T> 文档泛型类型
     * @return 不重复值的数量
     */
    @Override
    public <T> int cardinalityCount(final Object requestBuilder, final Func1<T, ?> column, final Class<T> clz) {
        if (!(requestBuilder instanceof SearchRequest.Builder searchRequestBuilder)) {
            throw new ElasticsearchException("request 入参必须为 co.elastic.clients.elasticsearch.core.SearchRequest.Builder 类型");
        }
        String indexName = getIndexName(clz);
        searchRequestBuilder.index(indexName);
        searchRequestBuilder.size(0);
        String columnName = ITemplate.getColumnName(column);
        String key = "cardinality_" + columnName + NormalTypeConstant.STR_UNDERLINE_COUNT;
        searchRequestBuilder.aggregations(key, agg -> agg.cardinality(b -> b.field(columnName)));
        SearchRequest searchRequest = searchRequestBuilder.build();
        final SearchResponse<JSONObject> response = executeByLog(
            searchRequest,
            request -> elasticsearchClient.search(request, JSONObject.class)
        );
        return Optional.ofNullable(response.aggregations().get(key))
            .map(Aggregate::cardinality)
            .map(CardinalityAggregate::value)
            .map(Long::intValue)
            .orElse(0)
            ;
    }

    /**
     * 计算不重复值的数量（精确值）
     *
     * <p>使用脚本化度量聚合计算字段的去重数量，结果为精确值。
     *
     * @param requestBuilder 查询条件构建器
     * @param column 列选择函数
     * @param clz 文档类型
     * @param <T> 文档泛型类型
     * @return 不重复值的数量
     */
    @Override
    public <T> int distinctCount(final Object requestBuilder, final Func1<T, ?> column, final Class<T> clz) {
        if (!(requestBuilder instanceof SearchRequest.Builder searchRequestBuilder)) {
            throw new ElasticsearchException("request 入参必须为 co.elastic.clients.elasticsearch.core.SearchRequest.Builder 类型");
        }
        String indexName = getIndexName(clz);
        searchRequestBuilder.index(indexName);
        searchRequestBuilder.size(0);
        String columnName = ITemplate.getColumnName(column);
        String key = "distinct_" + columnName + NormalTypeConstant.STR_UNDERLINE_COUNT;
        searchRequestBuilder.aggregations(key, buildScriptedMetricAggregation(columnName));
        SearchRequest searchRequest = searchRequestBuilder.build();
        final SearchResponse<JSONObject> response = executeByLog(
            searchRequest,
            request -> elasticsearchClient.search(request, JSONObject.class)
        );
        return Optional.ofNullable(response.aggregations().get(key))
            .map(Aggregate::scriptedMetric)
            .map(ScriptedMetricAggregate::value)
            .map(o -> o.to(Integer.class))
            .orElse(0)
            ;
    }

    /**
     * 游标查询（首次查询）
     *
     * <p>通过 {@link ISearchResult} 获取其他结果。scrollId 游标 ID 用于获取下一批数据的标记，第一次查询游标 ID 为空。
     * <p>ES6 使用 org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * <p>ES7 或 ES8 使用 co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param requestBuilder 查询条件构建器
     * @param clz 文档类型
     * @param timeSeconds 游标 ID 查询的有效时间（单位：秒）
     * @param <T> 文档泛型类型
     * @return SearchResult 对象
     */
    @Override
    public <T> ISearchResult<T> scrollSearchByRequestBuilder(final Object requestBuilder,
                                                              final Class<T> clz,
                                                              final Long timeSeconds) {
        String indexName = getIndexName(clz);
        if (!(requestBuilder instanceof SearchRequest.Builder searchRequestBuilder)) {
            throw new ElasticsearchException("request 入参必须为 co.elastic.clients.elasticsearch.core.SearchRequest.Builder 类型");
        }
        SearchRequest searchRequest = SearchRequest.of(builder -> searchRequestBuilder
            .index(indexName)
            .scroll(Time.of(b -> b.time(timeSeconds + "s")))
        );
        final SearchResponse<JSONObject> response = executeByLog(
            searchRequest,
            request -> elasticsearchClient.search(request, JSONObject.class)
        );
        TotalHits totalHits = response.hits().total();
        if ((totalHits == null || totalHits.value() <= 0) && CharSequenceUtil.isNotEmpty(response.scrollId())) {
            clearScroll(response.scrollId());
        }
        return convertResponseToResult(response, clz);
    }

    /**
     * 游标查询（续查）
     *
     * <p>通过 scrollId 继续查询下一批数据。
     *
     * @param scrollId 游标 ID（第一次查询时游标 ID 可为空）
     * @param clz 文档类型
     * @param timeSeconds 游标 ID 查询的有效时间（单位：秒）
     * @param <T> 文档泛型类型
     * @return SearchResult 对象
     */
    @Override
    public <T> ISearchResult<T> scrollSearchByScrollId(final String scrollId,
                                                        final Class<T> clz,
                                                        final Long timeSeconds) {
        ScrollRequest scrollRequest = ScrollRequest.of(builder -> builder
            .scrollId(scrollId)
            .scroll(Time.of(timeBuilder -> timeBuilder.time(timeSeconds + "s")))
        );
        final ScrollResponse<JSONObject> response = executeByLog(
            scrollRequest,
            request -> elasticsearchClient.scroll(request, JSONObject.class)
        );
        TotalHits totalHits = response.hits().total();
        if ((totalHits == null || totalHits.value() <= 0) && CharSequenceUtil.isNotEmpty(response.scrollId())) {
            clearScroll(response.scrollId());
        }
        return convertResponseToResult(response, clz);
    }

    /**
     * 清除滚动搜索的 scroll 上下文
     *
     * @param scrollId 需要清除的第一个 scroll ID
     * @param scrollIds 需要清除的其他 scroll ID（可变参数）
     * @return 如果清除操作成功则返回 true，否则返回 false
     */
    @SneakyThrows
    @Override
    public boolean clearScroll(final String scrollId, final String... scrollIds) {
        List<String> scrollIdList;
        if (ArrayUtil.isEmpty(scrollIds)) {
            scrollIdList = List.of(scrollId);
        } else {
            scrollIdList = new ArrayList<>(scrollIds.length + 1);
            scrollIdList.add(scrollId);
            scrollIdList.addAll(Arrays.asList(scrollIds));
        }
        ClearScrollRequest clearScrollRequest =
            ClearScrollRequest.of(builder -> builder.scrollId(scrollIdList));
        ClearScrollResponse clearScrollResponse = elasticsearchClient.clearScroll(clearScrollRequest);
        return clearScrollResponse.succeeded();
    }

    /**
     * 根据唯一字段查询单条记录
     *
     * @param column 字段名
     * @param value 字段值
     * @param clz 文档类型
     * @param <T> 文档泛型类型
     * @return 查询结果，如果不存在则返回 null
     */
    @Override
    public <T> T get(final String column, final Serializable value, final Class<T> clz) {
        BoolQuery.Builder boolQuery = boolQueryMustTermBuilder(column, value);
        SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder()
            .query(query -> query.bool(boolQuery.build()))
            .size(1);
        return searchOne(searchRequestBuilder, clz);
    }

    /**
     * 通过 ID 查询文档
     *
     * @param id 主键
     * @param clz 文档类型
     * @param <T> 文档泛型类型
     * @return 查询到的文档，如果不存在则返回 null
     */
    @Override
    public <T> T getById(final Serializable id, final Class<T> clz) {
        String indexName = getIndexName(clz);
        GetRequest getRequest = GetRequest.of(builder -> builder.id(String.valueOf(id)).index(indexName));
        final GetResponse<JSONObject> response = executeByLog(
            getRequest,
            request -> elasticsearchClient.get(request, JSONObject.class)
        );
        return convertResponseToResult(response, clz).asDocument();
    }

    /**
     * 批量通过 ID 查询文档
     *
     * <p>使用 mGet 方法批量构建 get 请求。
     * 使用 co.elastic.clients.elasticsearch.core.MGetRequest.Builder 作为入参。
     *
     * @param ids ID 集合，索引名优先使用 @Document 中的
     * @param clz 文档类型
     * @param <T> 文档泛型类型
     * @return 查询结果列表
     */
    @Override
    public <T> List<T> listByIds(final Collection<? extends Serializable> ids, final Class<T> clz) {
        String indexName = getIndexName(clz);
        List<String> idList = ids.stream().map(String::valueOf).toList();
        MgetRequest mgetRequest = MgetRequest.of(builder -> builder.ids(idList).index(indexName));
        if (mgetRequest.ids() == null || mgetRequest.ids().stream().noneMatch(CharSequenceUtil::isNotBlank)) {
            return new ArrayList<>(0);
        }
        final MgetResponse<JSONObject> response = executeByLog(
            mgetRequest,
            request -> elasticsearchClient.mget(request, JSONObject.class)
        );
        return convertResponseToResult(response, clz).asList();
    }

    /**
     * 根据字段查询文档列表
     *
     * @param column 字段名
     * @param value 字段值
     * @param clz 文档类型
     * @param <T> 文档泛型类型
     * @return 查询结果列表
     */
    @Override
    public <T> List<T> listByColumn(final String column, final Serializable value, final Class<T> clz) {
        BoolQuery.Builder boolQuery = boolQueryMustTermBuilder(column, value);
        SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder()
            .query(query -> query.bool(boolQuery.build()))
            .size(config.getWindowSizeOrDefault());
        return searchList(searchRequestBuilder, clz);
    }

    /**
     * 根据字段批量查询文档列表
     *
     * @param column 字段名
     * @param values 字段值集合
     * @param clz 文档类型
     * @param <T> 文档泛型类型
     * @return 查询结果列表
     */
    @Override
    public <T> List<T> listByColumn(final String column,
                                     final Collection<? extends Serializable> values,
                                     final Class<T> clz) {
        if (CollUtil.isEmpty(values)) {
            return Collections.emptyList();
        }
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        TermsQuery termsQuery = new TermsQuery.Builder()
            .field(column)
            .terms(b -> b.value(values.stream().map(FieldValue::of).toList()))
            .build();
        boolQuery.must(termsQuery._toQuery());
        SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder()
            .query(query -> query.bool(boolQuery.build()))
            .size(config.getWindowSizeOrDefault());
        return searchList(searchRequestBuilder, clz);
    }

    /**
     * 插入单个文档
     *
     * @param document 文档对象，类需要被 {@link Entity} 标记，字段需要被 {@link Column} 标记出主键
     * @return 操作结果
     */
    @Override
    public String insert(final IEntity<String> document) {
        final Class<?> aClass = document.getClass();
        final String indexName = getIndexName(aClass);
        final List<DocumentData> documentDataList = convertDocumentData(aClass, document);
        DocumentData documentData = documentDataList.getFirst();
        IndexRequest<?> indexRequest = IndexRequest.of(builder -> builder
            .index(indexName)
            .id(documentData.getId())
            .document(documentData.getSource())
        );
        IndexResponse response = executeByLog(
            indexRequest,
            request -> this.elasticsearchClient.index(request)
        );
        return response.result().jsonValue();
    }

    /**
     * 批量插入文档
     *
     * @param documents 文档集合
     * @return 如果全部插入成功则返回 true，否则返回 false
     */
    @Override
    public boolean insert(final Collection<IEntity<String>> documents) {
        if (CollUtil.isEmpty(documents)) {
            return true;
        }
        Class<?> documentClass = documents.iterator().next().getClass();
        final String indexName = getIndexName(documentClass);
        final List<DocumentData> documentDataList = convertDocumentData(documentClass, documents);
        BulkRequest bulkRequest = BulkRequest.of(builder -> {
            for (DocumentData documentData : documentDataList) {
                builder.operations(BulkOperation.of(op -> op
                    .index(idx -> idx
                        .index(indexName) // 目标索引
                        .id(documentData.getId()) // 文档ID（可选）
                        .document(documentData.getSource()) // 文档内容
                    )));
            }
            return builder;
        });
        BulkResponse response = executeByLog(
            bulkRequest,
            request -> this.elasticsearchClient.bulk(request)
        );
        return !response.errors();
    }

    /**
     * 按 ID 批量删除文档
     *
     * @param ids 需要删除的 ID 集合
     * @param documentClass 文档类，需要被 {@link Entity} 标记
     * @return 如果全部删除成功则返回 true，否则返回 false
     */
    @Override
    public boolean deleteById(final Collection<String> ids, final Class<? extends IEntity<String>> documentClass) {
        final String indexName = getIndexName(documentClass);
        BulkRequest bulkRequest = BulkRequest.of(builder -> {
            for (String id : ids) {
                builder.operations(BulkOperation.of(op -> op
                    .delete(b -> b
                        .id(id) // 文档ID
                        .index(indexName) // 目标索引
                    )));
            }
            return builder;
        });
        BulkResponse response = executeByLog(
            bulkRequest,
            request -> this.elasticsearchClient.bulk(request)
        );
        return !response.errors();
    }

    /**
     * 创建 HTTP 主机实例
     *
     * @param uri URI 对象
     * @return HttpHost 实例
     */
    private HttpHost createHttpHost(final URI uri) {
        if (!StringUtils.hasLength(uri.getUserInfo())) {
            return HttpHost.create(uri.toString());
        }
        try {
            return HttpHost.create(new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(),
                uri.getQuery(), uri.getFragment()).toString());
        } catch (URISyntaxException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * 构建 bool 查询的 must term 条件
     *
     * @param column 字段名
     * @param value 字段值
     * @return BoolQuery.Builder 实例
     */
    private BoolQuery.Builder boolQueryMustTermBuilder(final String column, final Serializable value) {
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        TermQuery.Builder builder = new TermQuery.Builder().field(column);
        switch (value) {
            case Short v -> builder.value(v);
            case Integer v -> builder.value(v);
            case Long v -> builder.value(v);
            case Float v -> builder.value(v);
            case Double v -> builder.value(v);
            case Number v -> builder.value(v.doubleValue());
            case Boolean v -> builder.value(v);
            case FieldValue v -> builder.value(v);
            case null, default -> builder.value(String.valueOf(value));
        }
        TermQuery termQuery = builder.build();
        boolQuery.must(termQuery._toQuery());
        return boolQuery;
    }

    /**
     * 构建精确去重脚本聚合
     *
     * <p>占用的空间和查询条件过滤后的数据集成正比，适合中大型数据量。
     *
     * @param fieldName 需要去重的字段名
     * @return ScriptedMetricAggregation 函数，使用此不需要 lambda 表达式
     */
    public static Function<Aggregation.Builder, ObjectBuilder<Aggregation>> buildScriptedMetricAggregation(final String fieldName) {
        return builder -> builder.scriptedMetric(scriptedMetricAggregation(fieldName));
    }

    /**
     * 创建精确去重脚本聚合
     *
     * <p>占用的空间和查询条件过滤后的数据集成正比，适合中大型数据量。
     *
     * @param fieldName 需要去重的字段名
     * @return ScriptedMetricAggregation 实例
     */
    public static ScriptedMetricAggregation scriptedMetricAggregation(final String fieldName) {
        ScriptedMetricAggregation.Builder builder = new ScriptedMetricAggregation.Builder();
        //初始化HashSet
        String initScript = "state.distinct = new HashSet();";
        //蒋文档的目标字段提取出来，加入到 单机的 HashSet
        String mapScript = "if (doc['" + fieldName + "'].size() > 0) state.distinct.add(doc['" + fieldName + "'].value);";
        String combineScript = "return state.distinct;";
        //所有的机器的hashSet合并到一个机器上,计算不同的个数
        String reduceScript = "HashSet result = new HashSet(); for (state in states) { result.addAll(state); } return result.size();";
        return scriptedMetricAggregation(builder, initScript, mapScript, combineScript, reduceScript);
    }

    /**
     * 构建精确去重脚本聚合（带聚合条件）
     *
     * <p>占用的空间和查询条件过滤后的数据集成正比。
     *
     * @param aggregationCondition 聚合条件
     * @return ScriptedMetricAggregation 函数，使用此不需要 lambda 表达式
     */
    public static Function<Aggregation.Builder, ObjectBuilder<Aggregation>> buildScriptedMetricAggregation(
        final AggregationCondition aggregationCondition) {
        return builder -> builder.scriptedMetric(scriptedMetricAggregation(aggregationCondition));
    }

    /**
     * 创建精确去重脚本聚合（带聚合条件）
     *
     * <p>占用的空间和查询条件过滤后的数据集成正比。
     *
     * @param aggregationCondition 聚合条件
     * @return ScriptedMetricAggregation 实例
     */
    public static ScriptedMetricAggregation scriptedMetricAggregation(final AggregationCondition aggregationCondition) {
        String fieldName = aggregationCondition.getFieldName();
        SortOrder sortOrder = aggregationCondition.getSortOrder();
        int limit = aggregationCondition.getLimit();
        ScriptedMetricAggregation.Builder builder = new ScriptedMetricAggregation.Builder();
        String initScript = "state.statistics = new HashMap();";
        String mapScriptTemplate;
        Object[] args;
        if (aggregationCondition instanceof SumAggregationCondition sumAggregationCondition) {
            mapScriptTemplate = ElasticsearchScriptConstants.MAP_SCRIPT_SUM_TEMPLATE;
            args = new String[]{fieldName, sumAggregationCondition.getSumFieldName(), fieldName, sumAggregationCondition.getSumFieldName()};
        } else {
            mapScriptTemplate = ElasticsearchScriptConstants.MAP_SCRIPT_COUNT_TEMPLATE;
            args = new String[]{fieldName, fieldName};
        }
        String mapScript = String.format(mapScriptTemplate, args);
        String combineScript = "return state.statistics;";
        String reduceScriptTemplate = ElasticsearchScriptConstants.REDUCE_SCRIPT_UNSORTED_TEMPLATE;
        if (SortOrder.DESCENDING.equals(sortOrder)) {
            reduceScriptTemplate = ElasticsearchScriptConstants.REDUCE_SCRIPT_DESCENDING_TEMPLATE;
        } else if (SortOrder.ASCENDING.equals(sortOrder)) {
            reduceScriptTemplate = ElasticsearchScriptConstants.REDUCE_SCRIPT_ASCENDING_TEMPLATE;
        }
        String reduceScript = String.format(reduceScriptTemplate, limit, limit).replace("doc_count", aggregationCondition.getNumberColumnName());
        return scriptedMetricAggregation(builder, initScript, mapScript, combineScript, reduceScript);
    }

    /**
     * 创建脚本化度量聚合
     *
     * @param builder 脚本化度量聚合构建器
     * @param initScript 初始化脚本
     * @param mapScript 映射脚本
     * @param combineScript 合并脚本
     * @param reduceScript 归约脚本
     * @return ScriptedMetricAggregation 实例
     */
    private static ScriptedMetricAggregation scriptedMetricAggregation(final ScriptedMetricAggregation.Builder builder,
                                                                        final String initScript,
                                                                        final String mapScript,
                                                                        final String combineScript,
                                                                        final String reduceScript) {
        builder.initScript(s -> s.inline(in -> in.source(initScript).lang(ElasticsearchScriptConstants.PAINLESS)))
            .mapScript(s -> s.inline(in -> in.source(mapScript).lang(ElasticsearchScriptConstants.PAINLESS)))
            .combineScript(s -> s.inline(in -> in.source(combineScript).lang(ElasticsearchScriptConstants.PAINLESS)))
            .reduceScript(s -> s.inline(in -> in.source(reduceScript).lang(ElasticsearchScriptConstants.PAINLESS)));
        return builder.build();
    }

}
