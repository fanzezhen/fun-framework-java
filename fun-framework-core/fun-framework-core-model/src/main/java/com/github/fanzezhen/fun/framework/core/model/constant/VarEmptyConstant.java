package com.github.fanzezhen.fun.framework.core.model.constant;

import com.alibaba.fastjson2.JSONObject;

/**
 * 变量空值常量
 *
 */
public final class VarEmptyConstant {
    public static final JSONObject EMPTY_JSON_OBJECT = new JSONObject();

    private VarEmptyConstant() {
        throw new UnsupportedOperationException("Utility class");
    }
}
