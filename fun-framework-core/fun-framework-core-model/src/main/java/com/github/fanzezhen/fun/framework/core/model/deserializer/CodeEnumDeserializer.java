package com.github.fanzezhen.fun.framework.core.model.deserializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.github.fanzezhen.fun.framework.core.model.service.ICode;

import java.lang.reflect.Type;

public class CodeEnumDeserializer implements ObjectDeserializer {
    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Integer code = parser.parseObject(Integer.class);
        for (ICode enumConstant : ((Class<? extends ICode>) type).getEnumConstants()) {
            if (enumConstant.getCode().equals(code)) {
                return (T) enumConstant;
            }
        }
        return null;
    }
}
