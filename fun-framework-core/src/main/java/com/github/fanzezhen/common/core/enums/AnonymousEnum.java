package com.github.fanzezhen.fun.framework.core.enums;

import lombok.Getter;

/**
 * @author fanzezhen
 * 是否匿名
 */
public enum AnonymousEnum {
    /**
     * 匿名
     */
    ANONYMOUS_YES(0, "匿名"),
    /**
     * 非匿名
     */
    ANONYMOUS_NO(1, "非匿名");

    public final int code;
    @Getter
    private final String desc;

    AnonymousEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
