package com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter;

import java.util.List;

/**
 *
 */
public interface IHitsAdapter {

    long getTotal();

    double getMaxScore();

    List<? extends IHit> getHitList();

}
