package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl;

import cn.hutool.core.bean.BeanUtil;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHit;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 命中数据持有者
 *
 * <p>封装 Elasticsearch 的 Hit 对象，提供统一的访问接口。
 */
@Data
public class HitHolder implements IHit {

    /**
     * Elasticsearch Hit 对象
     */
    private final Hit<?> hit;

    /**
     * 源数据映射
     */
    private final JSONObject sourceAsMap;

    /**
     * 文档 ID
     */
    private final String id;

    /**
     * 评分
     */
    private final Double score;

    /**
     * 高亮字段
     */
    private Map<String, List<String>> highlight;

    /**
     * 构造函数
     *
     * @param hit Elasticsearch Hit 对象
     */
    public HitHolder(final Hit<?> hit) {
        this.hit = hit;
        Object source = hit.source();
        if (source instanceof Map<?, ?> map) {
            this.sourceAsMap = new JSONObject( map);
        } else if (source != null) {
            this.sourceAsMap = new JSONObject( BeanUtil.beanToMap(source));
        } else {
            this.sourceAsMap = new JSONObject(0);
        }
        this.id = hit.id();
        this.score = hit.score();
        this.highlight = hit.highlight();
    }

    /**
     * Source内容
     */
    @Override
    public Object getSourceValue(String key) {
        //两种获取方式
        return Optional.ofNullable(sourceAsMap.get(key)).orElseGet(() -> hit.fields().get(key));
    }

    /**
     * 高亮字段
     */
    @Override
    public Map<String, List<String>> getHighlight() {
        return hit.highlight();
    }

    /**
     * 序列化行数据
     */
    @Override
    public String dataToString() {
        return JSON.toJSONString(sourceAsMap);
    }
}
