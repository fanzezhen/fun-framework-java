package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model;

import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHitsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHit;

import java.util.List;

/**
 *
 */
public class FieldHitsAdapter implements IHitsAdapter {
    private final List<? extends IHit> hitList;
    public FieldHitsAdapter(List<? extends IHit> hitList) {
        this.hitList = hitList;
    }

    @Override
    public long getTotal() {
        return 0;
    }

    @Override
    public double getMaxScore() {
        return 0;
    }

    @Override
    public List<? extends IHit> getHitList() {
        return hitList;
    }
}
