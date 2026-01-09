package com.github.fanzezhen.fun.framework.data.elasticsearch.base.model;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 文档数据对象
 *
 */
@ToString
@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class DocumentData {

    private String id;

    private JSONObject source;

}
