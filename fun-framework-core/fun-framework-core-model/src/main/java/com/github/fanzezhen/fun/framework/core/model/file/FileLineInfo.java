package com.github.fanzezhen.fun.framework.core.model.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 文件行信息
 *
 * @author fanzezhen
 * @since 3.4.3.5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FileLineInfo<T> {
    private String filename;
    private Integer number;
    private String content;
    private T data;
}
