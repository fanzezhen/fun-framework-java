package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model;

import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHitsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHit;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.HitHolder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 命中元数据适配器
 *
 * <p>将 Elasticsearch 的 HitsMetadata 适配为统一的 IHitsAdapter 接口。
 */
public class HitsMetadataAdapter implements IHitsAdapter {

    /**
     * 命中列表
     */
    private final List<IHit> hitList;

    /**
     * 总命中数
     */
    private final Long total;

    /**
     * 最大评分
     */
    private final Double maxScore;

    /**
     * 构造函数
     *
     * @param hitsMetadata 命中元数据
     */
    public HitsMetadataAdapter(final HitsMetadata<?> hitsMetadata) {
        this.hitList = Optional.ofNullable(hitsMetadata)
            .map(HitsMetadata::hits)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(HitHolder::new)
            .collect(Collectors.toList());

        this.total = Optional.ofNullable(hitsMetadata)
            .map(HitsMetadata::total)
            .map(TotalHits::value)
            .orElse(0L);

        this.maxScore = Optional.ofNullable(hitsMetadata)
            .map(HitsMetadata::maxScore)
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
        return hitList;
    }

}
