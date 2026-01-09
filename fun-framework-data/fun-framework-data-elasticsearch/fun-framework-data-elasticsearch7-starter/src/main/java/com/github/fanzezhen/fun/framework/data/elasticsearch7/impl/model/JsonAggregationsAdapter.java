package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model;

import cn.hutool.core.map.MapUtil;
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

public class JsonAggregationsAdapter implements IAggregationsAdapter {

    private final JSONObject aggregationsJson;

    public JsonAggregationsAdapter(Map<String, Object> aggregationsJson) {
        this.aggregationsJson = Optional.ofNullable(aggregationsJson).map(JSONObject::new).orElse(new JSONObject());
    }

    public IAggregationAdapter getAggregation(String name) {
        if (MapUtil.isEmpty(aggregationsJson)) {
            return null;
        }
        for (Map.Entry<String, Object> entry : aggregationsJson.entrySet()) {
            if (!(entry.getValue() instanceof JSONObject)) {
                continue;
            }
            final String key = entry.getKey();
            final int aggregationNameSplitIndex = key.indexOf("#");
            if (-1 == aggregationNameSplitIndex) {
                continue;
            }
            if (key.substring(aggregationNameSplitIndex + 1).equals(name)) {
                return new SearchAggregationAdapter((JSONObject) entry.getValue());
            }
        }
        return null;
    }

    static class SearchAggregationAdapter extends JsonAggregationsAdapter implements IAggregationAdapter {

        private final JSONObject aggregationJson;

        private final IHitsAdapter hitsAdapter;

        private final List<BucketAdapter> bucketList;

        public SearchAggregationAdapter(JSONObject aggregationJson) {
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

        private IHitsAdapter initHitsAdapter(JSONObject aggregationJson) {
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

        private List<BucketAdapter> initBucketList(JSONObject aggregationJson) {
            final JSONArray bucketsJson = aggregationJson.getJSONArray("buckets");

            if (Objects.isNull(bucketsJson)) {
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


    static class SearchBucketAdapter extends JsonAggregationsAdapter implements BucketAdapter {
        private final JSONObject bucketJson;

        public SearchBucketAdapter(JSONObject bucketJson) {
            super(bucketJson);
            this.bucketJson = bucketJson;
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

        @Override
        public <T> T get(String key, Class<T> tClass) {
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
