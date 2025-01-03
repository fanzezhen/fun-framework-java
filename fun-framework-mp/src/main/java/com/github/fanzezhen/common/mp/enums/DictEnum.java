package com.github.fanzezhen.fun.framework.mp.enums;

import com.github.fanzezhen.fun.framework.core.dict.AbstractDict;
import com.github.fanzezhen.fun.framework.mp.model.SysUserDict;
import lombok.Getter;

/**
 * @author zezhen.fan
 */
public enum DictEnum {
    /**
     * 系统用户字典
     */
    SYS_USER(SysUserDict.getInstance(), "系统用户字典");

    @Getter
    private final AbstractDict dict;
    @Getter
    private final String desc;

    DictEnum(AbstractDict dict, String desc) {
        this.dict = dict;
        this.desc = desc;
    }
}
