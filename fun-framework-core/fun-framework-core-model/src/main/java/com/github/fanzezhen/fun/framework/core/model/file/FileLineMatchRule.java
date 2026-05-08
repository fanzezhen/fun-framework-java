package com.github.fanzezhen.fun.framework.core.model.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * 文件行匹配规则
 * <p>
 * 定义文件逐行解析时的匹配条件和数据提取函数。
 * 泛型T用于累积解析状态，支持有状态的文件解析流程。
 * <p>
 * <b>使用场景：</b>日志文件解析、配置文件解析、结构化文本提取
 *
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
