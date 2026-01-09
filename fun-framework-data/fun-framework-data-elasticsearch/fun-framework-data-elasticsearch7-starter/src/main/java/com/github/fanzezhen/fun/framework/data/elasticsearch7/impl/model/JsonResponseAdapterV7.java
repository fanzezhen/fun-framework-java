package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model;

import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHitsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.impl.JsonSearchHitsAdapterImpl;

import java.util.Optional;

/**
 * JSONResponse解析，复用了SearchAggregationsAdapter的代码
 */
public class JsonResponseAdapterV7 implements IResponseAdapter {


    private final IAggregationsAdapter aggregations;
    private final IHitsAdapter hits;

    public JsonResponseAdapterV7(JSONObject jsonObject) {
        this.aggregations = Optional.ofNullable(jsonObject)
                .map(e -> e.getJSONObject("aggregations"))
                .map(JsonAggregationsAdapterV7::new)
                .orElse(null);
        this.hits = Optional.ofNullable(jsonObject)
                .map(e -> e.getJSONObject("hits"))
                .map(JsonSearchHitsAdapterImpl::new)
                .orElse(null);
    }

    /**
     * 获取聚合
     */
    @Override
    public IAggregationsAdapter getAggregationsAdapter() {
        return aggregations;
    }

    /**
     * 获取hits
     */
    @Override
    public IHitsAdapter getHitsAdapter() {
        return hits;
    }
}
