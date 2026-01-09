package com.github.fanzezhen.fun.framework.proxy.fastjson;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;

import java.lang.reflect.Type;

/**
 * 代理字段反序列化器（fastjson2 版本）
 *
 * @author fanzezhen
 * @since 3.4.3.5
 */
public class ProxyFieldReader implements ObjectReader<String> {

    private static volatile boolean enabled = false;
    private static ProxyHelper proxyHelper = null;

    @Override
    public String readObject(JSONReader reader, Type fieldType, Object fieldName, long features) {
        // 自动处理 null
        if (reader.nextIfNull()) {
            return null;
        }

        // 读取字符串（兼容 JSON 字符串或数字转字符串等场景）
        String value = reader.readString();

        // 如果启用代理，则进行装饰（注意：反序列化时 decorateStr 可能是“解密”或“还原”）
        return enabled && proxyHelper != null ? proxyHelper.decorateStr(value) : value;
    }

    static void initStatic(boolean enabled, ProxyHelper proxyHelper) {
        ProxyFieldReader.enabled = enabled;
        ProxyFieldReader.proxyHelper = proxyHelper;
    }
}
