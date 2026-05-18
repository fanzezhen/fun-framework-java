package com.github.fanzezhen.fun.framework.data.elasticsearch.base.dao;

import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.ISearchResult;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.template.IElasticsearchTemplate;
import jakarta.annotation.Resource;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Elasticsearch DAO 抽象基类
 * <p>
 * 提供 ES 文档的基础 CRUD 和查询操作，封装了 elasticsearchTemplate 的调用。
 * 子类需实现 getDocumentClass() 方法指定文档实体类型。
 * </p>
 * <p>版本兼容：支持 ES6、ES7、ES8，查询方法的入参根据 ES 版本自动适配</p>
 * <p>使用示例：</p>
 * <pre>
 * public class UserEsDao extends AbstractEsDao&lt;UserDocument&gt; {
 *     {@literal @}Override
 *     public Class&lt;UserDocument&gt; getDocumentClass() {
 *         return UserDocument.class;
 *     }
 * }
 * </pre>
 *
 * @param <T> 文档实体类型，必须继承 IEntity&lt;String&gt;
 */
public abstract class AbstractEsDao<T extends IEntity<String>> {

    /**
     * Elasticsearch 操作模板
     */
    @Resource
    protected IElasticsearchTemplate elasticsearchTemplate;

    /**
     * 根据字段和值查询单个文档
     *
     * @param func 字段选择函数
     * @param value 字段值
     * @return 查询到的文档，如果不存在则返回 null
     */
    public T get(final Func1<T, ?> func, final Serializable value) {
        return elasticsearchTemplate.get(func, value, getDocumentClass());
    }

    /**
     * 根据 ID 查询单个文档
     *
     * @param id 文档 ID
     * @return 查询到的文档，如果不存在则返回 null
     */
    public T getById(final String id) {
        return elasticsearchTemplate.getById(id, getDocumentClass());
    }

    /**
     * 根据 ID 列表批量查询文档
     *
     * @param ids 文档 ID 集合
     * @return 查询到的文档列表
     */
    public List<T> listByIds(final Collection<String> ids) {
        return elasticsearchTemplate.listByIds(ids, getDocumentClass());
    }

    /**
     * 根据 ID 集合批量删除文档
     *
     * @param ids 要删除的文档 ID 集合
     * @return 如果删除成功返回 true，否则返回 false
     */
    public boolean deleteById(final Collection<String> ids) {
        return elasticsearchTemplate.deleteById(ids, getDocumentClass());
    }
    /**
     * 查询单个文档
     * <p>
     * 版本兼容：
     * <ul>
     *   <li>ES6: org.elasticsearch.search.builder.SearchSourceBuilder 作为入参</li>
     *   <li>ES7/ES8: co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参</li>
     * </ul>
     * </p>
     *
     * @param request 查询条件
     * @return 查询结果，如果不存在则返回 null
     */
    public T searchOne(final Object request) {
        return elasticsearchTemplate.searchOne(request, getDocumentClass());
    }

    /**
     * 查询多个文档
     * <p>
     * 版本兼容：
     * <ul>
     *   <li>ES6: org.elasticsearch.search.builder.SearchSourceBuilder 作为入参</li>
     *   <li>ES7/ES8: co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参</li>
     * </ul>
     * </p>
     *
     * @param request 查询条件
     * @return 查询结果列表
     */
    public List<T> searchList(final Object request) {
        return elasticsearchTemplate.searchList(request, getDocumentClass());
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
     * @return SearchResult 对象
     */
    public ISearchResult<T> search(final Object requestBuilder) {
        return elasticsearchTemplate.search(requestBuilder, getDocumentClass());
    }

    /**
     * 游标查询，通过 {@link ISearchResult} 获取其他结果
     * <p>
     * 使用 scrollId 进行分页查询，第一次查询时 scrollId 为空。
     * 版本兼容：
     * <ul>
     *   <li>ES6: org.elasticsearch.search.builder.SearchSourceBuilder 作为入参</li>
     *   <li>ES7/ES8: co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参</li>
     * </ul>
     * </p>
     *
     * @param requestBuilder 查询条件构建器
     * @param timeSeconds 游标查询的有效时间（单位：秒）
     * @return SearchResult 对象
     */
    public ISearchResult<T> scrollSearchByRequestBuilder(final Object requestBuilder, final Long timeSeconds) {
        return elasticsearchTemplate.scrollSearchByRequestBuilder(requestBuilder, getDocumentClass(), timeSeconds);
    }

    /**
     * 游标查询，通过 {@link ISearchResult} 获取其他结果
     * <p>
     * 使用已有的 scrollId 继续查询下一批数据。
     * </p>
     *
     * @param scrollId 游标 ID（第一次查询时可为空）
     * @param timeSeconds 游标查询的有效时间（单位：秒）
     * @return SearchResult 对象
     */
    public ISearchResult<T> scrollSearchByScrollId(final String scrollId, final Long timeSeconds) {
        return elasticsearchTemplate.scrollSearchByScrollId(scrollId, getDocumentClass(), timeSeconds);
    }

    /**
     * 游标查询，通过 {@link ISearchResult} 获取其他结果
     * <p>
     * 根据 scrollId 是否为空自动选择查询方式：
     * <ul>
     *   <li>scrollId 为空：使用 requestBuilder 发起新的游标查询</li>
     *   <li>scrollId 不为空：使用 scrollId 继续查询下一批数据</li>
     * </ul>
     * </p>
     *
     * @param requestBuilder 查询条件构建器
     * @param timeSeconds 游标查询的有效时间（单位：秒）
     * @param scrollId 游标 ID（第一次查询时可为空）
     * @return SearchResult 对象
     */
    public ISearchResult<T> scrollSearch(final Object requestBuilder, final Long timeSeconds, final String scrollId) {
        if (CharSequenceUtil.isNotEmpty(scrollId)) {
            return elasticsearchTemplate.scrollSearchByScrollId(scrollId, getDocumentClass(), timeSeconds);
        }
        return elasticsearchTemplate.scrollSearchByRequestBuilder(requestBuilder, getDocumentClass(), timeSeconds);
    }

    /**
     * Elasticsearch 多重搜索，按照请求返回多个结果
     * <p>
     * 一次性执行多个查询请求，提高查询效率。
     * </p>
     *
     * @param requestBuilders 多个不同的请求构建器集合
     *                        (co.elastic.clients.elasticsearch.core.SearchRequest.Builder)
     * @return 按照 requestBuilders 的顺序返回包装的结果列表，如果某个 request 没有值也会有一个空对象
     */
    public List<ISearchResult<T>> mSearch(final Collection<?> requestBuilders) {
        return elasticsearchTemplate.mSearch(requestBuilders, getDocumentClass());
    }

    /**
     * 获取文档实体类型
     * <p>
     * 子类必须实现此方法以指定具体的文档类型。
     * </p>
     *
     * @return 文档实体的 Class 对象
     */
    public abstract Class<T> getDocumentClass();
}
