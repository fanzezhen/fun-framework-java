package com.github.fanzezhen.fun.framework.core.model.constant;

import java.util.regex.Pattern;

/**
 * 正则表达式常量
 *
 */
public class RegexConstant {
    /**
     * 正则表达式：匹配 ${...}
     */
    public static final Pattern PATTERN_$_CURLY_BRACKET_COMPATIBLE_APOSTROPHE = Pattern.compile("'\\$\\{([^{}]+)}'|\\$\\{([^{}]+)}");

    private RegexConstant() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

}
