package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model;

import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.BaseSearchResult;

import java.util.Optional;

/**
 * 解析es的json返回格式，适配于es7版本，和6的区别在于total取值逻辑
 */
public class JsonSearchResultImplV7<T> extends BaseSearchResult<T, JSONObject> {

    private final JsonResponseAdapterV7 responseAdapter;

    private final long totalHits;

    private final double totalTime;
    private final String scrollId;

    public JsonSearchResultImplV7(Class<T> tClass, JSONObject jsonObject) {
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
