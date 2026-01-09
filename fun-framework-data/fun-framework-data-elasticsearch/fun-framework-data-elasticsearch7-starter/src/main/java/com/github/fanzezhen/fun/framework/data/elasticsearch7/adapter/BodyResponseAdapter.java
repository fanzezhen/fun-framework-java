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
 *
 */
@Slf4j
public class BodyResponseAdapter implements IResponseAdapter {

    private final IAggregationsAdapter aggregations;

    private final IHitsAdapter hits;

        public BodyResponseAdapter(ResponseBody<?> searchResponse) {
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
