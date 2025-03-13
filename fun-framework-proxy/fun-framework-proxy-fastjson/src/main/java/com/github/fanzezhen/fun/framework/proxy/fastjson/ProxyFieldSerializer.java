package com.github.fanzezhen.fun.framework.proxy.fastjson;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 代理字段解析器
 *
 * @author fanzezhen
 * @createTime 2025/2/17 11:27
 * @since 3.4.3.5
 */
public class ProxyFieldSerializer implements ObjectSerializer {
    private static boolean enabled;
    private static ProxyHelper proxyHelper;

    /**
     * fastjson invokes this call-back method during serialization when it encounters a field of the
     * specified type.
     *
     * @param object    src the object that needs to be converted to Json.
     * @param fieldName parent object field name
     * @param fieldType parent object field type
     * @param features  parent object field serializer features
     */
    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
            return;
        }
        String strVal = object.toString();
        strVal = enabled ? proxyHelper.decorateStr(strVal) : strVal;
        out.writeString(strVal);
    }

    public static void initStatic(boolean enabled, ProxyHelper proxyHelper) {
        ProxyFieldSerializer.enabled = enabled;
        ProxyFieldSerializer.proxyHelper = proxyHelper;
    }
}
