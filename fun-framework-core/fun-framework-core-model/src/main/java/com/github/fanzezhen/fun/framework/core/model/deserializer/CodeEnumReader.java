package com.github.fanzezhen.fun.framework.core.model.deserializer;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.github.fanzezhen.fun.framework.core.model.enums.ICodeEnum;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 * FastJson2 枚举反序列化器
 * <p>
 * 支持从整数或字符串数字解析为 ICodeEnum 枚举。
 * 当 code 不匹配任何枚举值时返回 null 而非抛出异常，保证反序列化鲁棒性。
 * </p>
 */
@Slf4j
public class CodeEnumReader implements ObjectReader<ICodeEnum<?>> {

    /**
     * 反序列化 ICodeEnum 枚举
     * <p>
     * 支持从整数或字符串数字形式的 code 值解析为枚举对象。
     * 当输入为 null、空字符串或不匹配任何枚举值时返回 null。
     * </p>
     *
     * @param reader    JSON 读取器
     * @param fieldType 字段类型（枚举类型）
     * @param fieldName 字段名称
     * @param features  特性标志
     * @return 枚举对象，解析失败时返回 null
     */
    @Override
    @SuppressWarnings("unchecked")
    public ICodeEnum<?> readObject(JSONReader reader, Type fieldType, Object fieldName, long features) {
        if (reader.nextIfNull()) {
            return null;
        }
        Integer code;
        try {
            // 尝试读取为整数（兼容字符串数字）
            if (reader.isString()) {
                String str = reader.readString();
                code = str.isEmpty() ? null : Integer.valueOf(str);
            } else {
                code = reader.readInt32();
            }
        } catch (Exception e) {
            log.warn("", e);
            return null;
        }
        if (code == null) return null;
        for (ICodeEnum<?> enumConstant : ((Class<? extends ICodeEnum<?>>) fieldType).getEnumConstants()) {
            if (enumConstant.getCode().equals(code)) {
                return enumConstant;
            }
        }
        log.error("枚举解析失败 {} {}", fieldName, fieldType);
        return null;
    }
}
