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
 *
 */
public class HitsMetadataAdapter implements IHitsAdapter {

    private final List<IHit> hitList;

    private final Long total;

    private final Double maxScore;

    public HitsMetadataAdapter(HitsMetadata<?> hitsMetadata) {
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
