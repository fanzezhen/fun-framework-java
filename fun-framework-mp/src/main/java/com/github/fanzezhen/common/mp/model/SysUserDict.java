package com.github.fanzezhen.fun.framework.mp.model;


import com.github.fanzezhen.fun.framework.core.dict.AbstractDict;
import com.github.fanzezhen.fun.framework.mp.enums.SexEnum;

import java.util.LinkedHashMap;

/**
 * @author zezhen.fan
 */
public class SysUserDict extends AbstractDict {

    @Override
    public void init() {
        put("username", "用户名");
        put("email", "邮箱");
        put("phone", "手机号");
        put("sex", "性别");
    }

    @Override
    protected void initReverse() {
        putReverse("用户名", "username");
        putReverse("手机号", "phone");
        putReverse("邮箱", "email");
        putReverse("性别", "sex");
    }

    @Override
    protected void initBeWrapped() {
        putFieldWrapper("sex", SexEnum.WOMAN.code, SexEnum.WOMAN.getDesc());
        putFieldWrapper("sex", SexEnum.MAN.code, SexEnum.MAN.getDesc());
        putFieldWrapper("sex", SexEnum.UNKNOWN.code, SexEnum.UNKNOWN.getDesc());
        putFieldWrapper("sex", SexEnum.UNSPECIFIED.code, SexEnum.UNSPECIFIED.getDesc());
    }

    @Override
    protected void initBeAdapter() {
        putFieldAdapter("sex", new LinkedHashMap<>(4, 1) {{
            put(SexEnum.WOMAN.getDesc(), SexEnum.WOMAN.code);
            put(SexEnum.MAN.getDesc(), SexEnum.MAN.code);
            put(SexEnum.UNKNOWN.getDesc(), SexEnum.UNKNOWN.code);
            put(SexEnum.UNSPECIFIED.getDesc(), SexEnum.UNSPECIFIED.code);
        }});
    }

    private static class SingletonHolder {
        public static SysUserDict INSTANCE = new SysUserDict();
    }

    public static SysUserDict getInstance() {
        return SingletonHolder.INSTANCE;
    }

}
