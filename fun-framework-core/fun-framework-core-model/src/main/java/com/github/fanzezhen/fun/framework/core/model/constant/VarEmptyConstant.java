package com.github.fanzezhen.fun.framework.core.model.constant;

import com.alibaba.fastjson2.JSONObject;

/**
 * 变量空值常量
 * <p>
 * 定义常用的空值常量，如空 JSON 对象等。
 * </p>
 */
public final class VarEmptyConstant {
    /**
     * 空 JSON 对象常量
     */
    public static final JSONObject EMPTY_JSON_OBJECT = new JSONObject();

    /**
     * 工具类不允许实例化
     */
    private VarEmptyConstant() {
        throw new UnsupportedOperationException("Utility class");
    }
}
