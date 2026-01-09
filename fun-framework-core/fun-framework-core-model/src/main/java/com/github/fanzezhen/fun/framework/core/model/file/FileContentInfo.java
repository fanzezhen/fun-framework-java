package com.github.fanzezhen.fun.framework.core.model.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

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
public class FileContentInfo<T> {
    private String filename;
    private String aliasName;
    private List<HitLine> hitLineList;
    private T data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class HitLine {
        private String ruleName;
        private String content;
        private Integer number;
    }

    public synchronized FileContentInfo<T> addHitLine(String ruleName, String lineContent, Integer number) {
        if (hitLineList == null) {
            hitLineList = new ArrayList<>();
        }
        hitLineList.add(new HitLine(ruleName, lineContent, number));
        return this;
    }

    @SuppressWarnings("unchecked")
    public static <T> FileContentInfo<T> empty() {
        return (FileContentInfo<T>) INSTANCE.EMPTY;
    }

    public static class INSTANCE {
        public static final FileContentInfo<?> EMPTY = new FileContentInfo<>();
    }
}
