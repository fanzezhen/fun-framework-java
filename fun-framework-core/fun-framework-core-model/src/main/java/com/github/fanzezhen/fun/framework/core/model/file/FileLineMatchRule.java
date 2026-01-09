package com.github.fanzezhen.fun.framework.core.model.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

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
public class FileLineMatchRule<T> {
    private String name;
    private BiPredicate<String, T> predicate;
    private BiFunction<String, T, T> extractFunction;
}
