package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model;

import co.elastic.clients.json.JsonData;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHit;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 */
public class FieldHit implements IHit {

    private final Map<String, Object> sourceAsMap;
    private final Map<String, JsonData> fields;

    private final String id;

    public FieldHit(String id, Map<String, JsonData> fields, JSONObject source) {
        this.id = id;
        this.fields = fields;
        this.sourceAsMap = source;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Double getScore() {
        return 100D;
    }

    @Override
    public Object getSourceValue(String key) {
        //两种获取方式
        return Optional.ofNullable(sourceAsMap.get(key))
            .orElseGet(() -> fields.get(key));
    }

    @Override
    public Map<String, List<String>> getHighlight() {
        return Collections.emptyMap();
    }

    /**
     * 序列化行数据
     */
    @Override
    public String dataToString() {
        return JSON.toJSONString(sourceAsMap);
    }

}
