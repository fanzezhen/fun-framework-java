package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson2.JSON;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHitsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHit;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 */
public class SearchHitsAdapter implements IHitsAdapter {

    private final List<IHit> hits;

    private final long total;

    private final double maxScore;

    public SearchHitsAdapter(SearchHits searchHits) {
        this.hits = Optional.ofNullable(searchHits)
                .map(SearchHits::getHits)
                .map(Arrays::stream)
                .orElse(Stream.empty())
                .map(HitAdapter::new)
                .collect(Collectors.toList());

        this.total = Optional.ofNullable(searchHits)
                .map(SearchHits::getTotalHits)
                .map(e -> e.value)
                .orElse(0L);

        this.maxScore = Optional.ofNullable(searchHits)
                .map(SearchHits::getMaxScore)
                .map(Double::valueOf)
                .orElse(-1.0D);
    }

    @Override
    public long getTotal() {
        return total;
    }

    @Override
    public double getMaxScore() {
        return maxScore;
    }

    @Override
    public List<IHit> getHitList() {
        return hits;
    }

    static class HitAdapter implements IHit {

        private final SearchHit searchHit;

        private final Map<String, Object> sourceAsMap;

        private final String id;

        private final double score;

        private Map<String, List<String>> highlight;

        public HitAdapter(SearchHit searchHit) {
            this.searchHit = searchHit;
            this.sourceAsMap = Optional.ofNullable(searchHit.getSourceAsMap()).orElse(Collections.emptyMap());

            this.id = searchHit.getId();
            this.score = searchHit.getScore();
        }

        /**
         * es id
         */
        @Override
        public String getId() {
            return id;
        }

        /**
         * 得分
         */
        @Override
        public Double getScore() {
            return score;
        }

        /**
         * Source内容
         */
        @Override
        public Object getSourceValue(String key) {
            //两种获取方式
            return Optional.ofNullable(sourceAsMap.get(key))
                    .orElseGet(() -> Optional.ofNullable(searchHit.getFields().get(key))
                            .map(DocumentField::getValue)
                            .orElse(null)
                    );
        }

        /**
         * 高亮字段
         */
        @Override
        public Map<String, List<String>> getHighlight() {
            if (Objects.nonNull(highlight)) {
                return highlight;
            }

            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            highlight = HashMap.newHashMap(highlightFields.size());
            if (MapUtil.isEmpty(highlightFields)) {
                return Collections.emptyMap();
            }
            for (Map.Entry<String, HighlightField> entry : highlightFields.entrySet()) {
                List<String> textList = highlight.get(entry.getKey());
                if (CollectionUtils.isEmpty(textList)) {
                    textList = new ArrayList<>();
                    highlight.put(entry.getKey(), textList);
                }
                Text[] fragments = entry.getValue().getFragments();
                for (Text fragment : fragments) {
                    textList.add(fragment.string());
                }
            }
            return highlight;
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
