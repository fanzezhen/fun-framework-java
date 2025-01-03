package com.github.fanzezhen.fun.framework.core.enums.file;

import lombok.Getter;

/**
 * @author fanzezhen
 * 附件类型枚举类
 */
public enum AttachmentTypeEnum {
    /**
     * 图片
     */
    PICTURE(0, "图片"),
    /**
     * 文件
     */
    FILE(1, "文件");

    public final int code;
    @Getter
    private final String desc;

    AttachmentTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
