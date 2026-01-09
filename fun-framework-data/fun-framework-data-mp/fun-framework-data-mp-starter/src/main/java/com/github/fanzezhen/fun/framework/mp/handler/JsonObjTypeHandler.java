package com.github.fanzezhen.fun.framework.mp.handler;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonObjTypeHandler extends AbstractFastjsonTypeHandler<JSONObject> {

    public JsonObjTypeHandler() {
        super(JSONObject.class);
    }
}
