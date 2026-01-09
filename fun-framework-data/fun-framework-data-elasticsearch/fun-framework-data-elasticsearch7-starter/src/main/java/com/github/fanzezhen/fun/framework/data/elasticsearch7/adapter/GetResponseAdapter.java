package com.github.fanzezhen.fun.framework.data.elasticsearch7.adapter;

import co.elastic.clients.elasticsearch.core.GetResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHitsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHit;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 */
public class GetResponseAdapter implements IResponseAdapter {

    private final IHitsAdapter hitsAdapter;

    public GetResponseAdapter(GetResponse<JSONObject> getResponse) {
        this.hitsAdapter = Optional.ofNullable(getResponse)
                .map(Collections::singletonList)
                .map(Hits::new)
                .orElse(null);
    }

    /**
     * 获取聚合
     */
    @Override
    public IAggregationsAdapter getAggregationsAdapter() {
        return null;
    }

    /**
     * 获取hits
     */
    @Override
    public IHitsAdapter getHitsAdapter() {
        return hitsAdapter;
    }

    static class Hits implements IHitsAdapter {

        private final List<? extends Hit> hitList;

        private final long total;

        public Hits(Collection<GetResponse<JSONObject>> getResponses) {
            this.hitList = Optional.ofNullable(getResponses)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(GetResponseAdapter.Hit::new)
                    .toList();

            this.total = Optional.ofNullable(getResponses)
                    .map(Collection::size)
                    .orElse(0);
        }

        @Override
        public long getTotal() {
            return total;
        }

        @Override
        public double getMaxScore() {
            return 100;
        }

        @Override
        public List<? extends Hit> getHitList() {
            return hitList;
        }
    }

    static class Hit implements IHit {

        private final GetResponse<JSONObject> getResponse;

        private final Map<String, Object> sourceAsMap;

        private final String id;

        public Hit(GetResponse<JSONObject> getResponse) {
            this.getResponse = getResponse;
            this.sourceAsMap = Optional.ofNullable(getResponse)
                    .map(GetResponse::source)
                    .orElse(new JSONObject());

            this.id = Optional.ofNullable(getResponse)
                    .map(GetResponse::id)
                    .orElse(null);
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
                    .orElseGet(() -> Optional.ofNullable(getResponse)
                            .map(e -> e.fields().get(key))
                            .orElse(null)
                    );
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
}
