package com.github.fanzezhen.fun.framework.proxy.fastjson;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;

import java.lang.reflect.Type;

/**
 * 代理字段解析器（fastjson2 版本）
 *
 * @author fanzezhen
 * @since 3.4.3.5
 */
public class ProxyFieldWriter implements ObjectWriter<Object> {
    private static volatile boolean enabled = false;
    private static ProxyHelper proxyHelper = null;

    @Override
    public void write(JSONWriter writer, Object object, Object fieldName, Type fieldType, long features) {
        if (object == null) {
            writer.writeNull();
            return;
        }

        String strVal = object.toString();
        if (enabled && proxyHelper != null) {
            strVal = proxyHelper.decorateStr(strVal);
        }
        writer.writeString(strVal);
    }

    public static void initStatic(boolean enabled, ProxyHelper proxyHelper) {
        ProxyFieldWriter.enabled = enabled;
        ProxyFieldWriter.proxyHelper = proxyHelper;
    }
}
