package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.BucketAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHitsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.AggregationFieldEnum;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.BucketFieldEnum;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.IElasticsearchFieldEnum;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * JSON 聚合适配器
 *
 * <p>将 JSON 格式的聚合结果适配为统一的 IAggregationsAdapter 接口。
 */
public class JsonAggregationsAdapter implements IAggregationsAdapter {

    /**
     * 聚合 JSON 对象
     */
    private final JSONObject aggregationsJson;

    /**
     * 构造函数
     *
     * @param aggregationsJson 聚合 JSON 映射
     */
    public JsonAggregationsAdapter(final Map<String, Object> aggregationsJson) {
        this.aggregationsJson = Optional.ofNullable(aggregationsJson).map(JSONObject::new).orElse(new JSONObject());
    }

    /**
     * 获取指定名称的聚合适配器
     *
     * @param name 聚合名称
     * @return 聚合适配器，如果不存在则返回 null
     */
    public IAggregationAdapter getAggregation(final String name) {
        if (MapUtil.isEmpty(aggregationsJson)) {
            return null;
        }
        boolean isAny = CharSequenceUtil.isEmpty(name);
        for (Map.Entry<String, Object> entry : aggregationsJson.entrySet()) {
            final String key = entry.getKey();
            final int aggregationNameSplitIndex = key.indexOf("#");
            if (entry.getValue() instanceof JSONObject aggregationJson
                && aggregationNameSplitIndex != -1
                && (isAny || key.substring(aggregationNameSplitIndex + 1).equals(name))) {
                return new SearchAggregationAdapter(aggregationJson);
            }
        }
        return null;
    }

    /**
     * 搜索聚合适配器内部类
     */
    static class SearchAggregationAdapter extends JsonAggregationsAdapter implements IAggregationAdapter {

        /**
         * 聚合 JSON 对象
         */
        private final JSONObject aggregationJson;

        /**
         * 命中数据适配器
         */
        private final IHitsAdapter hitsAdapter;

        /**
         * 桶列表
         */
        private final List<BucketAdapter> bucketList;

        /**
         * 构造函数
         *
         * @param aggregationJson 聚合 JSON 对象
         */
        public SearchAggregationAdapter(final JSONObject aggregationJson) {
            super(aggregationJson);
            this.aggregationJson = aggregationJson;
            this.hitsAdapter = initHitsAdapter(aggregationJson);
            this.bucketList = initBucketList(aggregationJson);
        }

        @Override
        public List<BucketAdapter> getBuckets() {
            return bucketList;
        }

        @Override
        public IHitsAdapter getHits() {
            return hitsAdapter;
        }

        @Override
        public String getName() {
            return aggregationJson.getString("name");
        }

        @Override
        public String getType() {
            return aggregationJson.getString("type");
        }

        @Override
        public <T> T get(String key, Class<T> tClass) {
            return aggregationJson.getObject(key, tClass);
        }

        @Override
        public <T> T get(AggregationFieldEnum aggregationField, Class<T> tClass) {
            return aggregationJson.getObject(aggregationField.getKey(), tClass);
        }

        /**
         * 初始化命中数据适配器
         *
         * @param aggregationJson 聚合 JSON 对象
         * @return 命中数据适配器
         */
        private IHitsAdapter initHitsAdapter(final JSONObject aggregationJson) {
            final JSONObject hitsJson = aggregationJson.getJSONObject("hits");
            if (Objects.isNull(hitsJson)) {
                return null;
            }
            final JSONArray innerHits = hitsJson.getJSONArray("hits");
            SearchHit[] searchHitArray = new SearchHit[innerHits.size()];
            for (int i = 0; i < innerHits.size(); i++) {
                final JSONObject hitJson = innerHits.getJSONObject(i);
                Map<String, DocumentField> fields = new HashMap<>();
                final JSONObject source = hitJson.getJSONObject("_source");
                for (Map.Entry<String, Object> entry : source.entrySet()) {
                    DocumentField documentField = new DocumentField(entry.getKey(), Collections.singletonList(entry.getValue()));
                    fields.put(entry.getKey(), documentField);
                }
                Map<String, DocumentField> metaFields = new HashMap<>();
                for (String metaField : IElasticsearchFieldEnum.META_FIELDS) {
                    DocumentField documentField = new DocumentField(metaField, Collections.singletonList(hitJson.get(metaField)));
                    fields.put(metaField, documentField);
                }
                final SearchHit searchHit = new SearchHit(i, hitJson.getString("_id"), new Text(hitJson.getString("_type")), fields, metaFields);
                searchHitArray[i] = searchHit;
            }
            final JSONObject total = hitsJson.getJSONObject("total");
            final SearchHits searchHits = new SearchHits(searchHitArray
                    , new TotalHits(total.getLongValue("value"), TotalHits.Relation.EQUAL_TO)
                    , hitsJson.getFloatValue("max_score"));
            return new SearchHitsAdapter(searchHits);
        }

        /**
         * 初始化桶列表
         *
         * @param aggregationJson 聚合 JSON 对象
         * @return 桶适配器列表
         */
        private List<BucketAdapter> initBucketList(final JSONObject aggregationJson) {
            JSONArray bucketsJson = aggregationJson.getJSONArray("buckets");
            if (bucketsJson == null) {
                try {
                    bucketsJson = aggregationJson.getJSONArray("value");
                } catch (Exception ignored) {
                    // 忽略异常
                }
            }
            if (bucketsJson == null || bucketsJson.isEmpty()) {
                return Collections.emptyList();
            }

            List<BucketAdapter> bucketAdapterList = new ArrayList<>(bucketsJson.size());
            for (int i = 0; i < bucketsJson.size(); i++) {
                final JSONObject bucketJson = bucketsJson.getJSONObject(i);
                bucketAdapterList.add(new SearchBucketAdapter(bucketJson));
            }
            return bucketAdapterList;
        }

    }


    /**
     * 搜索桶适配器内部类
     */
    static class SearchBucketAdapter extends JsonAggregationsAdapter implements BucketAdapter {

        /**
         * 桶 JSON 对象
         */
        private final JSONObject bucketJson;

        /**
         * 构造函数
         *
         * @param bucketJson 桶 JSON 对象
         */
        public SearchBucketAdapter(final JSONObject bucketJson) {
            super(bucketJson);
            this.bucketJson = bucketJson;
        }

        @Override
        public JSONObject getBucketJson() {
            return bucketJson;
        }

        @Override
        public int getInt(BucketFieldEnum aggregationField) {
            return bucketJson.getIntValue(aggregationField.getKey());
        }

        @Override
        public long getLong(BucketFieldEnum aggregationField) {
            return bucketJson.getLongValue(aggregationField.getKey());
        }

        @Override
        public String getString(BucketFieldEnum aggregationField) {
            return bucketJson.getString(aggregationField.getKey());
        }

        @Override
        public double getDouble(BucketFieldEnum aggregationField) {
            return bucketJson.getDoubleValue(aggregationField.getKey());
        }

        /**
         * 获取指定键的值
         *
         * @param key 键名
         * @param tClass 目标类型
         * @param <T> 目标泛型类型
         * @return 转换后的值
         */
        @Override
        @SuppressWarnings("unchecked")
        public <T> T get(final String key, final Class<T> tClass) {
            if (Map.class.isAssignableFrom(tClass)) {
                final JSONObject jsonObject = bucketJson.getJSONObject(key);
                if (Objects.isNull(jsonObject)) {
                    return null;
                }
                return (T) new JSONObject(jsonObject);
            }
            return bucketJson.getObject(key, tClass);
        }

        @Override
        public <T> T get(BucketFieldEnum bucketField, Class<T> tClass) {
            return this.get(bucketField.getKey(), tClass);
        }

    }

}
