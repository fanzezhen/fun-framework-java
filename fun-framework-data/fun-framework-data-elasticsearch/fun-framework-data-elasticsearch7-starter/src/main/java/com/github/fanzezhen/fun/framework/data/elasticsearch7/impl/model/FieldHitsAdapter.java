package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model;

import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHitsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHit;

import java.util.List;

/**
 * 字段命中数据适配器
 *
 * <p>提供字段命中数据的统一访问接口。
 */
public class FieldHitsAdapter implements IHitsAdapter {

    /**
     * 命中列表
     */
    private final List<? extends IHit> hitList;

    /**
     * 构造函数
     *
     * @param hitList 命中列表
     */
    public FieldHitsAdapter(final List<? extends IHit> hitList) {
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
