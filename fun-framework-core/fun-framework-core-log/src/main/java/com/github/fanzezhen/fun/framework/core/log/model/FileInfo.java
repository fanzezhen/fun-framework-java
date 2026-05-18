package com.github.fanzezhen.fun.framework.core.log.model;

import lombok.Data;

/**
 * 用于日志记录的文件信息对象.
 * <p>
 * 用于在日志输出中表示文件元数据，而不是记录整个文件内容。
 * 包含文件名、原始文件名和大小信息。
 */
@Data
public class FileInfo {

    /**
     * 文件描述常量.
     */
    private static final String DESC = "参数为文件类型，只打印文件描述信息";
    /**
     * 参数名称.
     */
    private String name;
    /**
     * 原始文件名.
     */
    private String fileName;
    /**
     * 文件大小（字节）.
     */
    private long size;

    /**
     * 构造文件信息对象.
     *
     * @param paramName 参数名称
     * @param originalFileName 原始文件名
     * @param fileSize 文件大小（字节）
     */
    public FileInfo(final String paramName, final String originalFileName, final long fileSize) {
        this.name = paramName;
        this.fileName = originalFileName;
        this.size = fileSize;
    }

    /**
     * 获取文件描述.
     *
     * @return 描述字符串
     */
    public String getDesc() {
        return DESC;
    }

}
