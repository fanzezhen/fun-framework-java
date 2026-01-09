package com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter;

import com.github.fanzezhen.fun.framework.core.data.model.IRow;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface IHit extends IRow {

    /**
     * es id
     */
    String getId();

    default Object getColumnSourceValue(String key) {
        return getSourceValue(key);
    }

    /**
     * 得分
     */
    Double getScore();

    /**
     * Source内容
     */
    Object getSourceValue(String key);

    /**
     * 高亮字段
     */
    Map<String, List<String>> getHighlight();

    /**
     * 序列化行数据
     */
    String dataToString();

}
