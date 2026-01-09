package com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHitsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * data层提供的解析es返回的命中文档
 */
public class JsonSearchHitsAdapterImpl implements IHitsAdapter {

    private final Optional<JSONObject> jsonOption;

    private final List<IHit> hits;

    public JsonSearchHitsAdapterImpl(JSONObject jsonObject) {
        this.jsonOption = Optional.ofNullable(jsonObject);
        this.hits = jsonOption
                .map(e -> e.getJSONArray("hits"))
                .map(array -> {
                    int size = array.size();
                    List<IHit> parsedHits = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        parsedHits.add(new HitImpl(array.getJSONObject(i)));
                    }
                    return parsedHits;
                }).orElse(new ArrayList<>());
    }

    @Override
    public long getTotal() {
        return jsonOption
                .map(e -> e.getJSONObject("total"))
                .map(e -> e.getLong("value"))
                .orElse(0L);
    }

    @Override
    public double getMaxScore() {
        return hits.stream().map(IHit::getScore)
                .mapToDouble(e -> e)
                .max()
                .orElse(0L);
    }

    @Override
    public List<IHit> getHitList() {
        return hits;
    }

    static class HitImpl implements IHit {

        private final Optional<JSONObject> jsonOption;

        HitImpl(JSONObject jsonObject) {
            this.jsonOption = Optional.ofNullable(jsonObject);
        }

        /**
         * es id
         */
        @Override
        public String getId() {
            return jsonOption.map(e -> e.getString("_id"))
                    .orElse(null);
        }

        /**
         * 得分
         */
        @Override
        public Double getScore() {
            return jsonOption.map(e -> e.getDouble("_score"))
                    .orElse(0D);
        }

        /**
         * Source内容
         *
         * @param key
         */
        @Override
        public Object getSourceValue(String key) {
            return jsonOption.map(e -> e.getJSONObject("_source"))
                    .map(e -> e.get(key))
                    .orElse(null);
        }

        /**
         * 高亮字段
         */
        @Override
        public Map<String, List<String>> getHighlight() {
            return jsonOption.map(e -> e.getJSONObject("highlight")).map(e -> {
                Map<String, List<String>> map = HashMap.newHashMap(e.size() * 2);
                for (Map.Entry<String, Object> entry : e.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof JSONArray jsonArray) {
                        int size = jsonArray.size();
                        List<String> values = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            values.add(jsonArray.getString(i));
                        }
                        map.put(key, values);
                    }
                }
                return map;
            }).orElse(HashMap.newHashMap(0));
        }

        /**
         * 序列化行数据
         */
        @Override
        public String dataToString() {
            return JSON.toJSONString(jsonOption.orElse(null));
        }
    }
}
