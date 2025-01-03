package com.github.fanzezhen.fun.framework.mp.handler;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractFastjsonTypeHandler<T extends JSON> extends AbstractJsonTypeHandler<T> {

    protected AbstractFastjsonTypeHandler(Class<T> classType) {
        super(classType);
    }

    @Override
    protected T parse(String jsonStr) {
        return JSON.parseObject(jsonStr, classType);
    }

    @Override
    protected String toJson(T obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONString(obj);
    }
}
