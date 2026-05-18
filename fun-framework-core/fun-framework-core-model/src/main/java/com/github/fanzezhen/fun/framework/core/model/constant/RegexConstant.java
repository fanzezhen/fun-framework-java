package com.github.fanzezhen.fun.framework.core.model.constant;

import java.util.regex.Pattern;

/**
 * 正则表达式常量
 * <p>
 * 定义常用的正则表达式模式，如匹配 ${...} 占位符等。
 * </p>
 */
public class RegexConstant {
    /**
     * 正则表达式：匹配 ${...} 占位符
     * <p>
     * 可以匹配 '${name}' 或 ${name} 两种格式，捕获花括号中的内容
     * </p>
     */
    public static final Pattern PATTERN_$_CURLY_BRACKET_COMPATIBLE_APOSTROPHE = Pattern.compile("'\\$\\{([^{}]+)}'|\\$\\{([^{}]+)}");

    /**
     * 工具类不允许实例化
     */
    private RegexConstant() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

}
