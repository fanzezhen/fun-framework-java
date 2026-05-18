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
 * 基于 JSON 的搜索命中记录适配器实现
 * <p>
 * 用于解析 Elasticsearch 返回的 JSON 格式命中文档集合
 */
public class JsonSearchHitsAdapterImpl implements IHitsAdapter {

    private final JSONObject jsonObject;

    private final List<IHit> hits;

    /**
     * 构造函数
     *
     * @param jsonObject Elasticsearch 响应中的 hits JSON 对象
     */
    public JsonSearchHitsAdapterImpl(final JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        this.hits = Optional.ofNullable(jsonObject)
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
        return Optional.ofNullable(jsonObject)
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

    /**
     * 命中记录实现类
     * <p>
     * 封装单条 Elasticsearch 命中记录的 JSON 数据
     */
    static class HitImpl implements IHit {

        private final JSONObject jsonObject;

        /**
         * 构造函数
         *
         * @param jsonObject 命中记录的 JSON 对象
         */
        HitImpl(final JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        /**
         * 获取文档 ID
         *
         * @return Elasticsearch 文档 ID
         */
        @Override
        public String getId() {
            return Optional.ofNullable(jsonObject).map(e -> e.getString("_id"))
                    .orElse(null);
        }

        /**
         * 获取文档得分
         *
         * @return 搜索得分
         */
        @Override
        public Double getScore() {
            return Optional.ofNullable(jsonObject).map(e -> e.getDouble("_score"))
                    .orElse(0D);
        }

        /**
         * 获取源数据字段值
         *
         * @param key 字段键
         * @return 字段值
         */
        @Override
        public Object getSourceValue(final String key) {
            return Optional.ofNullable(jsonObject).map(e -> e.getJSONObject("_source"))
                    .map(e -> e.get(key))
                    .orElse(null);
        }

        /**
         * 获取高亮字段映射
         *
         * @return 高亮字段映射，键为字段名，值为高亮片段列表
         */
        @Override
        public Map<String, List<String>> getHighlight() {
            return Optional.ofNullable(jsonObject).map(e -> e.getJSONObject("highlight")).map(e -> {
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
         * 将行数据序列化为字符串
         *
         * @return 序列化后的行数据字符串
         */
        @Override
        public String dataToString() {
            return JSON.toJSONString(jsonObject);
        }
    }
}
