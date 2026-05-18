package com.github.fanzezhen.fun.framework.data.elasticsearch7.adapter;

import cn.hutool.core.text.CharSequenceUtil;
import co.elastic.clients.elasticsearch.core.msearch.MultiSearchResponseItem;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHitsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.config.FunElasticsearch7AutoConfiguration;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model.HitsMetadataAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model.JsonAggregationsAdapter;
import jakarta.json.stream.JsonGenerator;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * MultiSearch 响应适配器
 *
 * <p>将 Elasticsearch 的 MultiSearchResponseItem 响应适配为统一的 IResponseAdapter 接口。
 */
@Slf4j
public class MultiSearchResponseAdapter implements IResponseAdapter {

    /**
     * 聚合适配器
     */
    private final IAggregationsAdapter aggregationsAdapter;

    /**
     * 命中数据适配器
     */
    private final IHitsAdapter hitsAdapter;

    /**
     * 构造函数
     *
     * @param response MultiSearch 响应项
     */
    public MultiSearchResponseAdapter(final MultiSearchResponseItem<JSONObject> response) {
        if (Objects.isNull(response)) {
            this.aggregationsAdapter = null;
            this.hitsAdapter = null;
        } else {
            this.hitsAdapter = new HitsMetadataAdapter(response.result().hits());
            JSONObject aggregationsJson;
            JacksonJsonpMapper jacksonJsonpMapper = FunElasticsearch7AutoConfiguration.getJacksonJsonpMapper();
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
                try (JsonGenerator generator = jacksonJsonpMapper.jsonProvider().createGenerator(byteArrayOutputStream)) {
                    response.serialize(generator, jacksonJsonpMapper);
                }
                String jsonSstring = byteArrayOutputStream.toString();
                JSONObject jsonObject = JSON.parseObject(jsonSstring);
                aggregationsJson = jsonObject.getJSONObject("aggregations");
            } catch (IOException e) {
                log.error("", e);
                aggregationsJson = null;
            }
            this.aggregationsAdapter = new JsonAggregationsAdapter(aggregationsJson);
        }
    }

    /**
     * 获取聚合
     */
    @Override
    public IAggregationsAdapter getAggregationsAdapter() {
        return aggregationsAdapter;
    }

    /**
     * 获取hits
     */
    @Override
    public IHitsAdapter getHitsAdapter() {
        return hitsAdapter;
    }

}
