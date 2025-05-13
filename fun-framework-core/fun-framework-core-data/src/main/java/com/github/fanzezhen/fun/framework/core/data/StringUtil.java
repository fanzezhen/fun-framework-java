package com.github.fanzezhen.fun.framework.core.data;

import cn.hutool.core.text.CharSequenceUtil;

import java.util.Arrays;
import java.util.Comparator;

/**
 * 字符串工具类
 *
 * @author fanzezhen
 * @since 3.4.3.5
 */
public class StringUtil {
    /**
     * 移除字符串前缀，优先移除最长的前缀
     */
    public static String removeAnyPrefix(CharSequence str, boolean ignoreCase, CharSequence... prefixes) {
        if (CharSequenceUtil.isNotEmpty(str)) {
            for (CharSequence prefix : Arrays.stream(prefixes).sorted(Comparator.comparingInt(CharSequence::length).reversed()).toArray(CharSequence[]::new)) {
                if (CharSequenceUtil.startWith(str, prefix, ignoreCase)) {
                    return CharSequenceUtil.subSuf(str, prefix.length());
                }
            }
        }
        return str.toString();
    }
    /**
     * 移除字符串前缀，优先移除最长的前缀
     */
    public static String removeAnyPrefix(CharSequence str, CharSequence... prefixes) {
        return removeAnyPrefix(str, false, prefixes);
    }
    /**
     * 移除字符串前缀，优先移除最长的前缀
     */
    public static String removeAnyPrefixIgnoreCase(CharSequence str, CharSequence... prefixes) {
        return removeAnyPrefix(str, true, prefixes);
    }
}
