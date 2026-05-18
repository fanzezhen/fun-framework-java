package com.github.fanzezhen.fun.framework.proxy.fastjson;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;

import java.lang.reflect.Type;

/**
 * 代理字段序列化器（fastjson2 版本）
 * <p>
 * 在 JSON 序列化时自动对字符串字段进行代理处理
 *
 * @since 3.4.3.5
 */
public class ProxyFieldWriter implements ObjectWriter<Object> {
    private static volatile boolean enabled = false;
    private static ProxyHelper proxyHelper = null;

    /**
     * 写入 JSON 对象并进行代理处理
     *
     * @param writer    JSON 写入器
     * @param object    待写入的对象
     * @param fieldName 字段名称
     * @param fieldType 字段类型
     * @param features  特性标志
     */
    @Override
    public void write(final JSONWriter writer,
                      final Object object,
                      final Object fieldName,
                      final Type fieldType,
                      final long features) {
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

    /**
     * 静态初始化方法，由配置类调用
     *
     * @param enabled     是否启用代理
     * @param proxyHelper 代理助手实例
     */
    public static void initStatic(final boolean enabled, final ProxyHelper proxyHelper) {
        ProxyFieldWriter.enabled = enabled;
        ProxyFieldWriter.proxyHelper = proxyHelper;
    }
}
