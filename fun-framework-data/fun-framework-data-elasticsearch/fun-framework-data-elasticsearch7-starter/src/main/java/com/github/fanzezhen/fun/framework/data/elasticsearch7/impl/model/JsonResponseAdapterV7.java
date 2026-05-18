package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model;

import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHitsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.impl.JsonSearchHitsAdapterImpl;

import java.util.Optional;

/**
 * ES7 JSON 响应适配器
 *
 * <p>用于解析 JSON 格式的响应，复用了 SearchAggregationsAdapter 的代码。
 */
public class JsonResponseAdapterV7 implements IResponseAdapter {

    /**
     * 聚合适配器
     */
    private final IAggregationsAdapter aggregations;

    /**
     * 命中数据适配器
     */
    private final IHitsAdapter hits;

    /**
     * 构造函数
     *
     * @param jsonObject JSON 对象
     */
    public JsonResponseAdapterV7(final JSONObject jsonObject) {
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
