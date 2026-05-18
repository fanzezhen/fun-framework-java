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
 * 字段命中数据
 *
 * <p>包含字段数据和源数据的命中记录。
 */
public class FieldHit implements IHit {

    /**
     * 源数据映射
     */
    private final Map<String, Object> sourceAsMap;

    /**
     * 字段数据映射
     */
    private final Map<String, JsonData> fields;

    /**
     * 文档 ID
     */
    private final String id;

    /**
     * 构造函数
     *
     * @param id 文档 ID
     * @param fields 字段数据映射
     * @param source 源数据
     */
    public FieldHit(final String id, final Map<String, JsonData> fields, final JSONObject source) {
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
