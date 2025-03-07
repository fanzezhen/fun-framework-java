package com.github.fanzezhen.fun.framework.core.model.enums;

import lombok.Getter;

/**
 * 文件类型
 *
 * @author fanzezhen
 * @since 3.0.0
 */
@Getter
public enum FileTypeEnum {
    /**
     * xlsx
     */
    JSON(".json"),
    /**
     * xlsx
     */
    XLSX(".xlsx");
    private final String prefix;

    FileTypeEnum(String prefix) {
        this.prefix = prefix;
    }
}
