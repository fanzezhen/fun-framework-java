package com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter;

import com.github.fanzezhen.fun.framework.core.model.common.IRow;

import java.util.List;
import java.util.Map;

/**
 * 命中记录接口
 * <p>
 * 表示 Elasticsearch 搜索结果中的单条命中记录，包含文档 ID、得分、源数据和高亮字段等信息
 */
public interface IHit extends IRow {

    /**
     * 获取文档 ID
     *
     * @return Elasticsearch 文档 ID
     */
    String getId();

    /**
     * 获取列源数据值
     * <p>
     * 默认实现委托给 {@link #getSourceValue(String)}
     *
     * @param key 列键
     * @return 列值
     */
    default Object getColumnSourceValue(final String key) {
        return getSourceValue(key);
    }

    /**
     * 获取文档得分
     *
     * @return 搜索得分
     */
    Double getScore();

    /**
     * 获取源数据字段值
     *
     * @param key 字段键
     * @return 字段值
     */
    Object getSourceValue(final String key);

    /**
     * 获取高亮字段映射
     *
     * @return 高亮字段映射，键为字段名，值为高亮片段列表
     */
    Map<String, List<String>> getHighlight();

    /**
     * 将行数据序列化为字符串
     *
     * @return 序列化后的行数据字符串
     */
    String dataToString();

}
