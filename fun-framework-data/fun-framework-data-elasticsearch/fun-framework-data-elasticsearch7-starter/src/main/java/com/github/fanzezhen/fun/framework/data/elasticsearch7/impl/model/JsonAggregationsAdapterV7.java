package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationAdapter;

import java.util.Map;

/**
 * ES7 JSON 聚合适配器
 *
 * <p>ES7 版本的 JSON 聚合查询结果适配器，不同点在于 JSON 聚合结果没有 # 的对象层级，
 * 通过重写 SearchAggregationsAdapter 的 getAggregation 方法实现。
 */
public class JsonAggregationsAdapterV7 extends JsonAggregationsAdapter {

    /**
     * 聚合 JSON 对象
     */
    private final JSONObject aggregationsJson;

    /**
     * 构造函数
     *
     * @param aggregationsJson 聚合 JSON 对象
     */
    public JsonAggregationsAdapterV7(final JSONObject aggregationsJson) {
        super(aggregationsJson);
        this.aggregationsJson = aggregationsJson;
    }

    /**
     * 获取指定名称的聚合适配器
     *
     * @param name 聚合名称
     * @return 聚合适配器，如果不存在则返回 null
     */
    @Override
    public IAggregationAdapter getAggregation(final String name) {
        boolean isAny = CharSequenceUtil.isEmpty(name);
        for (Map.Entry<String, Object> entry : aggregationsJson.entrySet()) {
            if (entry.getValue() instanceof JSONObject aggregationJson
                    && (isAny || entry.getKey().equals(name))) {
                return new SearchAggregationAdapter(aggregationJson);
            }
        }
        return null;
    }
}
