package com.github.fanzezhen.fun.framework.data.elasticsearch7.adapter;

import cn.hutool.core.text.CharSequenceUtil;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHitsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model.HitsMetadataAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model.JsonAggregationsAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * 响应体适配器
 *
 * <p>将 Elasticsearch 的 ResponseBody 响应适配为统一的 IResponseAdapter 接口。
 */
@Slf4j
public class BodyResponseAdapter implements IResponseAdapter {

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
     * @param searchResponse 搜索响应体
     */
    public BodyResponseAdapter(final ResponseBody<?> searchResponse) {
        this.aggregations = Optional.ofNullable(searchResponse)
            .map(ResponseBody::toString)
            .map(jsonStr -> {
                JSONObject jsonObject =
                    JSON.parseObject(jsonStr.replaceAll("^[A-Za-z]+Response:\\s*", CharSequenceUtil.EMPTY));
                return jsonObject.getJSONObject("aggregations");
            })
            .map(JsonAggregationsAdapter::new)
            .orElse(null);

        this.hits = Optional.ofNullable(searchResponse)
            .map(ResponseBody::hits)
            .map(HitsMetadataAdapter::new)
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
