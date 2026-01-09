package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model;

import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationAdapter;

import java.util.Map;

/**
 * es json的聚合查询结果，不同点在于json聚合结果没有#的对象层级，通过重写SearchAggregationsAdapter的getAggregation方法实现
 */
public class JsonAggregationsAdapterV7 extends JsonAggregationsAdapter {
    private final JSONObject aggregationsJson;

    public JsonAggregationsAdapterV7(JSONObject aggregationsJson) {
        super(aggregationsJson);
        this.aggregationsJson = aggregationsJson;
    }

    @Override
    public IAggregationAdapter getAggregation(String name) {
        for (Map.Entry<String, Object> entry : aggregationsJson.entrySet()) {
            if (!(entry.getValue() instanceof JSONObject)) {
                continue;
            }
            final String key = entry.getKey();
            if (key.equals(name)){
                return new SearchAggregationAdapter((JSONObject) entry.getValue());
            }
        }
        return null;
    }
}
