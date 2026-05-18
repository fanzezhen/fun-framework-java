package com.github.fanzezhen.fun.framework.data.elasticsearch.base.template;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.model.template.ITemplate;
import com.github.fanzezhen.fun.framework.core.log.support.FunLogHelper;
import com.github.fanzezhen.fun.framework.core.model.common.FunFunction;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.config.FunElasticsearchProperties;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IResponseDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.enums.FunDataElasticsearchExceptionEnum;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.BaseSearchResult;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.DocumentData;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.ISearchResult;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.serializer.IDocumentSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Elasticsearch 操作模板抽象基类
 * <p>
 * 提供 Elasticsearch 操作的通用实现，包括查询、搜索、滚动查询等功能。
 * 子类需要实现特定版本（ES6/ES7/ES8）的具体操作逻辑。
 * </p>
 * <p>该类封装了：</p>
 * <ul>
 *   <li>文档序列化和反序列化机制</li>
 *   <li>索引名称的自动处理</li>
 *   <li>日志记录功能</li>
 *   <li>响应结果的统一转换</li>
 * </ul>
 */
public abstract class BaseElasticsearchTemplate implements IElasticsearchTemplate {

    /**
     * Elasticsearch 数据源配置
     */
    protected final FunElasticsearchProperties.Config config;

    /**
     * 日志辅助工具
     */
    protected final FunLogHelper funLogHelper;

    /**
     * 文档序列化器列表
     */
    protected final List<IDocumentSerializer> documentSerializerList;

    /**
     * 响应反序列化器列表
     */
    protected final List<IResponseDeserializer> responseDeserializerList;

    /**
     * 构造方法
     *
     * @param config 数据源配置
     * @param funLogHelper 日志辅助工具
     * @param documentSerializerList 文档序列化器列表
     * @param responseDeserializerList 响应反序列化器列表
     */
    protected BaseElasticsearchTemplate(final FunElasticsearchProperties.Config config,
                                        final FunLogHelper funLogHelper,
                                        final List<IDocumentSerializer> documentSerializerList,
                                        final List<IResponseDeserializer> responseDeserializerList) {
        this.config = config;
        this.funLogHelper = funLogHelper;
        this.responseDeserializerList = responseDeserializerList;
        this.documentSerializerList = documentSerializerList;
    }
    /**
     * 高级查询，查询条件与 searchList 类似，但可以通过 {@link ISearchResult} 获取其他结果
     * <p>
     * 版本兼容：
     * <ul>
     *   <li>ES6: org.elasticsearch.search.builder.SearchSourceBuilder 作为入参</li>
     *   <li>ES7/ES8: co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参</li>
     * </ul>
     * </p>
     *
     * @param requestBuilder 查询条件构建器
     * @param clz 文档类型
     * @param <T> 返回文档类型
     * @return SearchResult 对象
     */
    @Override
    public <T> ISearchResult<T> search(final Object requestBuilder, final Class<T> clz) {
        final String indexName = getIndexName(clz);
        return search(requestBuilder, clz, indexName);
    }

    /**
     * 高级查询，查询条件与 searchList 类似，但可以通过 {@link ISearchResult} 获取其他结果
     * <p>
     * 版本兼容：
     * <ul>
     *   <li>ES6: org.elasticsearch.search.builder.SearchSourceBuilder 作为入参</li>
     *   <li>ES7/ES8: co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参</li>
     * </ul>
     * </p>
     *
     * @param requestBuilder 查询条件构建器
     * @param clz 文档类型
     * @param indexName 索引名称
     * @param <T> 返回文档类型
     * @return SearchResult 对象
     */
    public abstract <T> ISearchResult<T> search(Object requestBuilder, Class<T> clz, String indexName);

    /**
     * 清除滚动搜索的 scroll 上下文
     * <p>
     * 当滚动搜索完成后，应该及时清除 scroll 上下文以释放服务器资源。
     * </p>
     *
     * @param scrollId 主要的 scroll ID
     * @param scrollIds 其他需要清除的 scroll ID（可选）
     * @return 如果清除操作成功则返回 true，否则返回 false
     */
    public abstract boolean clearScroll(String scrollId, String... scrollIds);

    @Override
    public <T> T searchOne(final Object request, final Class<T> clz) {
        return search(request, clz).asDocument();
    }

    @Override
    public <T> List<T> searchList(final Object request, final Class<T> clz) {
        return search(request, clz).asDocumentList();
    }

    /**
     * 清除滚动搜索的 scroll 上下文
     * <p>
     * 当滚动搜索完成后，应该及时清除 scroll 上下文以释放服务器资源。
     * </p>
     *
     * @param clz 文档类型
     * @param scrollId 主要的 scroll ID
     * @param scrollIds 其他需要清除的 scroll ID（可选）
     * @param <T> 文档类型
     * @return 如果清除操作成功则返回 true，否则返回 false
     */
    @Override
    public <T> boolean clearScroll(final Class<T> clz, final String scrollId, final String... scrollIds) {
        return clearScroll(scrollId, scrollIds);
    }

    /**
     * 获取索引名称
     * <p>
     * 根据文档类型和配置的索引前缀生成完整的索引名称。
     * </p>
     *
     * @param clz 文档类型
     * @param <T> 文档类型
     * @return 索引名称
     */
    protected <T> String getIndexName(final Class<T> clz) {
        return ITemplate.getTable(clz, config.getIndexPrefix());
    }

    /**
     * 将文档对象转化为 DocumentData
     * <p>
     * 使用注册的序列化器将文档对象序列化为 ES 可识别的格式。
     * 序列化过程中会验证文档 ID 是否存在。
     * </p>
     *
     * @param aClass 文档对象类型
     * @param documents 文档对象数组
     * @return 解析后的文档数据列表
     * @throws SecurityException 如果找不到合适的序列化器或文档 ID 为空
     */
    protected List<DocumentData> convertDocumentData(final Class<?> aClass, final Object... documents) {
        final List<DocumentData> documentDataList = new ArrayList<>();
        for (final Object document : documents) {
            final DocumentData documentData = documentSerializerList.stream()
                .filter(e -> e.isSupport(document, aClass))
                .findFirst()
                .orElseThrow(() -> new SecurityException("不能解析此文档，可能是没有能够使用的序列化器：" + document))
                .serialize(document, aClass);
            // 检查id字段是否为空
            if (CharSequenceUtil.isBlank(documentData.getId())) {
                throw new SecurityException("更新的 Document 对象没有字段被标记 @EsId 注解标记，或者值为空：" + document);
            }
            documentDataList.add(documentData);
        }
        return documentDataList;
    }

    /**
     * 将文档对象集合转化为 DocumentData
     * <p>
     * 使用注册的序列化器将文档对象序列化为 ES 可识别的格式。
     * </p>
     *
     * @param aClass 文档对象类型
     * @param documents 文档对象集合
     * @return 解析后的文档数据列表
     * @throws SecurityException 如果找不到合适的序列化器或文档 ID 为空
     */
    protected List<DocumentData> convertDocumentData(final Class<?> aClass, final Collection<?> documents) {
        return convertDocumentData(aClass, documents.toArray());
    }

    /**
     * 将响应对象转化为 SearchResult
     * <p>
     * 使用注册的响应反序列化器将 ES 响应对象转换为统一的 SearchResult 格式。
     * 如果响应为空，返回空的 SearchResult 对象。
     * </p>
     *
     * @param response ES 响应对象
     * @param documentClass 文档类型
     * @param <T> 文档类型
     * @return 解析后的查询结果
     * @throws ServiceException 如果找不到合适的反序列化器
     */
    protected <T> ISearchResult<T> convertResponseToResult(final Object response, final Class<T> documentClass) {
        if (response == null) {
            return BaseSearchResult.empty(documentClass);
        }
        for (final IResponseDeserializer responseDeserializer : responseDeserializerList) {
            if (responseDeserializer.isSupport(response)) {
                return responseDeserializer.deserialize(response, documentClass);
            }
        }
        throw new ServiceException(FunDataElasticsearchExceptionEnum.RESPONSE_DESERIALIZER_ERROR,
                response.getClass(), CharSequenceUtil.EMPTY, "未注册");
    }

    /**
     * 执行操作并打印日志
     * <p>
     * 通过日志辅助工具记录操作的执行过程和结果。
     * </p>
     *
     * @param requestParam 请求参数
     * @param invoker 执行器函数
     * @param <T> 请求参数类型
     * @param <R> 返回结果类型
     * @return 执行结果
     */
    protected <T, R> R executeByLog(final T requestParam, final FunFunction<T, R> invoker) {
        return funLogHelper.executeByLog(
            this.getClass().getName(),
            invoker,
            requestParam
        );
    }
}
