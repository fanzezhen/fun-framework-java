package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
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
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransportBase;
import co.elastic.clients.transport.rest_client.RestClientOptions;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.data.annotation.Column;
import com.github.fanzezhen.fun.framework.core.data.annotation.Entity;
import com.github.fanzezhen.fun.framework.core.log.base.support.FunLogHelper;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.config.FunElasticsearchProperties;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IResponseDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.DocumentData;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.ISearchResult;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.serializer.IDocumentSerializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.template.BaseElasticsearchTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

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

/**
 * es Template 实现类
 *
 */
@Slf4j
public class ElasticsearchTemplateImpl extends BaseElasticsearchTemplate {

    private final JacksonJsonpMapper jacksonJsonpMapper;

    private ElasticsearchClient elasticsearchClient;

    public ElasticsearchTemplateImpl(FunElasticsearchProperties.Config config,
                                     FunLogHelper funLogHelper,
                                     JacksonJsonpMapper jacksonJsonpMapper,
                                     List<IDocumentSerializer> documentSerializerList,
                                     List<IResponseDeserializer> responseDeserializerList) {
        super(config, funLogHelper, documentSerializerList, responseDeserializerList);
        this.jacksonJsonpMapper = jacksonJsonpMapper;
        initElasticsearchClient();
    }

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
        // 设置为标准 application/json
        requestOptionsBuilder.removeHeader(HttpHeaders.CONTENT_TYPE);
        requestOptionsBuilder.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        // 可选：设置 Accept 头
        requestOptionsBuilder.removeHeader(HttpHeaders.ACCEPT);
        requestOptionsBuilder.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
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
                    "es/search",    // 单索引搜索
                    "es/msearch",   // 多索引搜索
                    "es/get",       // 获取文档
                    "es/mget",      // 多索引获取文档
                    "es/scroll",    // 滚动查询
                    "es/index",     // 新增/更新文档
                    "es/delete",    // 删除文档
                    "es/bulk",      // 批量操作
                    "es/count"      // 计数
                ));
            }
        } catch (Exception e) {
            throw new ServiceException("添加 ES 校验白名单失败", e);
        }
        // ========== 原有逻辑保持不变 ==========
        this.elasticsearchClient = new ElasticsearchClient(transport);
    }

    /**
     * 高级查询,查询条件与searchList类似,但可以通过{@link ISearchResult}获取其他结果
     * <p>
     * es6       org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * <p>
     * es7或es8  co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param requestBuilder 查询条件
     * @param clz            文档类型
     *
     * @return SearchResult对象
     */
    @Override
    public <T> ISearchResult<T> search(Object requestBuilder, Class<T> clz) {
        if (!(requestBuilder instanceof SearchRequest.Builder searchRequestBuilder)) {
            throw new ElasticsearchException("request 入参必须为 co.elastic.clients.elasticsearch.core.SearchRequest.Builder 类型");
        }
        String indexName = getIndexName(clz);
        SearchRequest searchRequest = SearchRequest.of(builder -> searchRequestBuilder.index(indexName));
        final SearchResponse<JSONObject> response = executeByLog(
            searchRequest,
            request -> elasticsearchClient.search(request, JSONObject.class)
        );
        return convertResponseToResult(response, clz);
    }

    /**
     * esMSearch,对按照请求返回多个结果
     *
     * @param requestBuilders 多个不同的请求build co.elastic.clients.elasticsearch.core.SearchRequest.Builder
     * @param clz             文档类型
     *
     * @return 按照requestBuilders的顺序逐一包装的返回结果，如果某个request没有值，也会有一个空对象
     */
    @Override
    public <T> List<ISearchResult<T>> mSearch(Collection<?> requestBuilders, Class<T> clz) {
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
                        if (searchRequest.aggregations()!=null) {
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
            .map(multiSearchResponseItem->
                convertResponseToResult(multiSearchResponseItem, clz))
            .toList();
    }

    /**
     * 游标查询，通过{@link ISearchResult}获取其他结果
     * scrollId 游标id用于获取下一批数据的标记，第一次查询游标id为空
     * <p>
     * es6       org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * <p>
     * es7或es8  co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param requestBuilder 查询条件
     * @param clz            文档类型
     * @param timeSeconds    游标id查询的有效时间（单位：分钟）
     *
     * @return SearchResult对象
     */
    @Override
    public <T> ISearchResult<T> scrollSearchByRequestBuilder(Object requestBuilder, Class<T> clz, Long timeSeconds) {
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
        return convertResponseToResult(response, clz);
    }

    /**
     * 游标查询，通过{@link ISearchResult}获取其他结果
     * scrollId 游标id用于获取下一批数据的标记，第一次查询游标id为空
     * <p>
     * es6       org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * <p>
     * es7或es8  co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param clz            文档类型
     * @param scrollId       游标id(第一次查询时游标id可为空)
     * @param timeSeconds    游标id查询的有效时间（单位：分钟）
     *
     * @return SearchResult对象
     */
    @Override
    public <T> ISearchResult<T> scrollSearchByScrollId(String scrollId, Class<T> clz, Long timeSeconds) {
        String indexName = getIndexName(clz);
        ScrollRequest scrollRequest = ScrollRequest.of(builder -> builder
            .scrollId(scrollId)
            .scroll(Time.of(timeBuilder -> timeBuilder.time(timeSeconds + "s")))
        );
        final ScrollResponse<JSONObject> response = executeByLog(
            scrollRequest,
            request -> elasticsearchClient.scroll(request, JSONObject.class)
        );
        return convertResponseToResult(response, clz);
    }

    /**
     * 根据唯一字段查询
     */
    @Override
    public <T> T get(String column, Serializable value, Class<T> clz) {
        BoolQuery.Builder boolQuery =boolQueryMustTermBuilder(column, value);
        SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder()
            .query(query -> query.bool(boolQuery.build()))
            .size(1);
        return searchOne(searchRequestBuilder, clz);
    }

    /**
     * 通过id查询文档
     *
     * @param id  主键
     * @param clz 文档类型
     *
     * @return 查询文档
     */
    @Override
    public <T> T getById(Serializable id, Class<T> clz) {
        String indexName = getIndexName(clz);
        GetRequest getRequest = GetRequest.of(builder -> builder.id(String.valueOf(id)).index(indexName));
        final GetResponse<JSONObject> response = executeByLog(
            getRequest,
            request -> elasticsearchClient.get(request, JSONObject.class)
        );
        return convertResponseToResult(response, clz).asDocument();
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
     * 根据唯一字段查询
     */
    @Override
    public <T> List<T> listByColumn(String column, Serializable value, Class<T> clz) {
        BoolQuery.Builder boolQuery =boolQueryMustTermBuilder(column, value);
        SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder()
            .query(query -> query.bool(boolQuery.build()))
            .size(config.getWindowSizeOrDefault());
        return searchList(searchRequestBuilder, clz);
    }

    /**
     * 根据唯一字段查询
     */
    @Override
    public <T> List<T> listByColumn(String column, Collection<? extends Serializable> values, Class<T> clz) {
        if (CollUtil.isEmpty(values)){
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
     * put 文档
     *
     * @param document 文档对象
     *                 类需要被 {@link Entity} 标记
     *                 字段需要被 {@link Column} 标记出主键
     */
    @Override
    public String insert(IEntity<String> document) {
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
     * 批量 put 文档
     *
     * @param documents 文档
     *
     * @return 插入成功数量
     */
    @Override
    public boolean insert(Collection<IEntity<String>> documents) {
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
     * 按条件删除索引数据
     *
     * @param ids           需要删除的id
     * @param documentClass 类需要被 {@link Entity} 标记
     *
     * @return 删除条数
     */
    @Override
    public boolean deleteById(Collection<String> ids, Class<? extends IEntity<String>> documentClass) {
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

    private HttpHost createHttpHost(URI uri) {
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

    private BoolQuery.Builder boolQueryMustTermBuilder(String column, Serializable value) {
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

}
