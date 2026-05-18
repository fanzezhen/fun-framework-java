package com.github.fanzezhen.fun.framework.proxy.fastjson;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;

import java.lang.reflect.Type;

/**
 * 代理字段反序列化器（fastjson2 版本）
 * <p>
 * 在 JSON 反序列化时自动对字符串字段进行代理处理
 *
 * @since 3.4.3.5
 */
public class ProxyFieldReader implements ObjectReader<String> {

    private static volatile boolean enabled = false;
    private static ProxyHelper proxyHelper = null;

    /**
     * 读取 JSON 对象并进行代理处理
     *
     * @param reader    JSON 读取器
     * @param fieldType 字段类型
     * @param fieldName 字段名称
     * @param features  特性标志
     * @return 处理后的字符串值
     */
    @Override
    public String readObject(final JSONReader reader,
                             final Type fieldType,
                             final Object fieldName,
                             final long features) {
        // 自动处理 null
        if (reader.nextIfNull()) {
            return null;
        }

        // 读取字符串（兼容 JSON 字符串或数字转字符串等场景）
        String value = reader.readString();

        // 如果启用代理，则进行装饰（注意：反序列化时 decorateStr 可能是”解密”或”还原”）
        return enabled && proxyHelper != null ? proxyHelper.decorateStr(value) : value;
    }

    /**
     * 静态初始化方法，由配置类调用
     *
     * @param enabled     是否启用代理
     * @param proxyHelper 代理助手实例
     */
    static void initStatic(final boolean enabled, final ProxyHelper proxyHelper) {
        ProxyFieldReader.enabled = enabled;
        ProxyFieldReader.proxyHelper = proxyHelper;
    }
}
