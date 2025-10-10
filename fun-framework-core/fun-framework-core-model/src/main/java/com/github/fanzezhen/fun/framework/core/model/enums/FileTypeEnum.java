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
     * word文档
     */
    DOCX(".docx"),
    /**
     * json
     */
    JSON(".json"),
    /**
     * excel
     */
    XLSX(".xlsx");
    private final String suffix;

    FileTypeEnum(String suffix) {
        this.suffix = suffix;
    }
}
