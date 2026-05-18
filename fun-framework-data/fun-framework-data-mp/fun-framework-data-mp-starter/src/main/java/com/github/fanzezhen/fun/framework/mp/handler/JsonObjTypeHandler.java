package com.github.fanzezhen.fun.framework.mp.handler;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * JSON 对象类型处理器
 * <p>
 * 基于 Fastjson 实现的 {@link JSONObject} 类型处理器，用于将数据库中的 JSON 字符串字段与 {@link JSONObject} 对象之间进行转换。
 * <p>
 * 使用方式：
 * <pre>{@code
 * @TableName(autoResultMap = true)
 * public class MyEntity {
 *     @TableField(typeHandler = JsonObjTypeHandler.class)
 *     private JSONObject extraData;
 * }
 * }</pre>
 */
@Slf4j
public class JsonObjTypeHandler extends AbstractFastjsonTypeHandler<JSONObject> {

    /**
     * 构造函数
     */
    public JsonObjTypeHandler() {
        super(JSONObject.class);
    }
}
