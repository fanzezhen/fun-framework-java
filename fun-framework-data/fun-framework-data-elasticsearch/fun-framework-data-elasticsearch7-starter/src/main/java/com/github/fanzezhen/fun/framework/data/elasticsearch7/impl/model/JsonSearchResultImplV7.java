package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model;

import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.BaseSearchResult;

import java.util.Optional;

/**
 * ES7 JSON 查询结果实现
 *
 * <p>解析 ES 的 JSON 返回格式，适配于 ES7 版本，和 ES6 的区别在于 total 取值逻辑。
 *
 * @param <T> 文档泛型类型
 */
public class JsonSearchResultImplV7<T> extends BaseSearchResult<T, JSONObject> {

    /**
     * 响应适配器
     */
    private final JsonResponseAdapterV7 responseAdapter;

    /**
     * 总命中数
     */
    private final long totalHits;

    /**
     * 总耗时
     */
    private final double totalTime;

    /**
     * 游标 ID
     */
    private final String scrollId;

    /**
     * 构造函数
     *
     * @param tClass 文档类型
     * @param jsonObject JSON 对象
     */
    public JsonSearchResultImplV7(final Class<T> tClass, final JSONObject jsonObject) {
        super(tClass);
        this.responseAdapter = new JsonResponseAdapterV7(jsonObject);

        Optional<JSONObject> hit = Optional.ofNullable(jsonObject).map(e -> e.getJSONObject("hits"));
        this.totalHits = hit.map(e -> e.getJSONObject("total"))
                .map(e -> e.getLong("value")).orElse(0L);
        long tookMills = hit.map(e -> e.getLong("took")).orElse(0L);
        this.totalTime = tookMills == 0L? 0L: (tookMills / 1000.0D);
        this.scrollId = hit.map(e -> e.getString("_scroll_id")).orElse(null);
    }

    /**
     * 总数量
     */
    @Override
    public long getTotalHits() {
        return totalHits;
    }

    /**
     * 总耗时
     */
    @Override
    public double getTotalTime() {
        return totalTime;
    }

    /**
     * 获取游标id
     */
    @Override
    public String getScrollId() {
        return scrollId;
    }

    @Override
    public IResponseAdapter getResponseAdapter() {
        return responseAdapter;
    }
}
