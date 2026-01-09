package com.github.fanzezhen.fun.framework.core.log.base.model;

import lombok.Data;

@Data
public class FileInfo {

    private static final String DESC = "参数为文件类型，只打印文件描述信息";
    private String name;
    private String fileName;
    private long size;

    public FileInfo(String name, String fileName, long size) {
        this.name = name;
        this.fileName = fileName;
        this.size = size;
    }

    public String getDesc() {
        return DESC;
    }

}
