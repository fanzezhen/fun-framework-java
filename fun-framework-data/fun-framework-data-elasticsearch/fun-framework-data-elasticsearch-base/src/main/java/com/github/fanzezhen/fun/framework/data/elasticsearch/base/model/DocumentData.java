package com.github.fanzezhen.fun.framework.data.elasticsearch.base.model;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 文档数据对象
 * <p>
 * 封装 Elasticsearch 文档的 ID 和源数据
 */
@ToString
@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class DocumentData {

    /**
     * 文档 ID
     */
    private String id;

    /**
     * 文档源数据
     */
    private JSONObject source;

}
