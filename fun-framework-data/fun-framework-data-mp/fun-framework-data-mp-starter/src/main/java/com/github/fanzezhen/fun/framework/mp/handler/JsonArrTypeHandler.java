package com.github.fanzezhen.fun.framework.mp.handler;

import com.alibaba.fastjson2.JSONArray;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonArrTypeHandler extends AbstractFastjsonTypeHandler<JSONArray> {

    public JsonArrTypeHandler() {
        super(JSONArray.class);
    }
}
