package com.github.fanzezhen.fun.framework.proxy.fastjson;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;

import java.lang.reflect.Type;

/**
 * 代理字段解析器
 *
 * @author fanzezhen
 * @createTime 2025/2/17 11:27
 * @since 3.4.3.5
 */
public class ProxyFieldDeserializer implements ObjectDeserializer {
    private static boolean enabled;
    private static ProxyHelper proxyHelper;

    @Override
    public String deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        String value = parser.parseObject(String.class);
        return enabled ? proxyHelper.decorateStr(value) : value;
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }

    public static void initStatic(boolean enabled, ProxyHelper proxyHelper) {
        ProxyFieldDeserializer.enabled = enabled;
        ProxyFieldDeserializer.proxyHelper = proxyHelper;
    }
}
