package com.github.fanzezhen.fun.framework.data.elasticsearch7.adapter;

import co.elastic.clients.elasticsearch.core.MgetResponse;
import co.elastic.clients.elasticsearch.core.get.GetResult;
import co.elastic.clients.elasticsearch.core.mget.MultiGetError;
import co.elastic.clients.elasticsearch.core.mget.MultiGetResponseItem;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHitsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model.FieldHit;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model.FieldHitsAdapter;

import java.util.List;
import java.util.Objects;

/**
 * MultiGet 响应适配器
 *
 * <p>将 Elasticsearch 的 MgetResponse 响应适配为统一的 IResponseAdapter 接口。
 */
public class MultiGetResponseAdapter implements IResponseAdapter {

    /**
     * 命中数据适配器
     */
    private final IHitsAdapter hits;

    /**
     * 构造函数
     *
     * @param response MultiGet 响应对象
     */
    public MultiGetResponseAdapter(final MgetResponse<JSONObject> response) {
        if (Objects.isNull(response)) {
            this.hits = null;
        } else {
            List<MultiGetResponseItem<JSONObject>> docs = response.docs();
            List<FieldHit> hitList =docs.stream().map(multiGetResponseItem -> {
                if (multiGetResponseItem.isFailure()){
                    MultiGetError failure = multiGetResponseItem.failure();
                    throw new ServiceException(failure.error().toString());
                }
                GetResult<JSONObject> result = multiGetResponseItem.result();
                return new FieldHit(result.id(), result.fields(), result.source());
            }).toList();
            this.hits = new FieldHitsAdapter(hitList);
        }
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
        return hits;
    }

}
