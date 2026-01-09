package com.github.fanzezhen.fun.framework.core.model.deserializer;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.github.fanzezhen.fun.framework.core.model.enums.ICodeEnum;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 */
@Slf4j
public class CodeEnumReader implements ObjectReader<ICodeEnum<?>> {

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
