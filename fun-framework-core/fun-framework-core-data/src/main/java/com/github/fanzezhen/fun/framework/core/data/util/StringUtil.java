package com.github.fanzezhen.fun.framework.core.data.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;

import java.util.Arrays;
import java.util.Comparator;

/**
 * 字符串工具类
 *
 * @author fanzezhen
 * @since 3.4.3.5
 */
@SuppressWarnings("unused")
public class StringUtil {
    private StringUtil() {
    }

    /**
     * 移除字符串前缀，只移除 prefixes 中第一个匹配到的前缀
     */
    public static String removeFirstPrefix(CharSequence str, boolean ignoreCase, CharSequence... prefixes) {
        if (CharSequenceUtil.isNotEmpty(str)) {
            for (CharSequence prefix : prefixes) {
                if (CharSequenceUtil.startWith(str, prefix, ignoreCase)) {
                    return CharSequenceUtil.subSuf(str, prefix.length());
                }
            }
        }
        return str.toString();
    }

    /**
     * 移除字符串前缀，只移除 prefixes 中第一个匹配到的前缀
     */
    public static String removeFirstPrefix(CharSequence str, CharSequence... prefixes) {
        return removeFirstPrefix(str, false, prefixes);
    }

    /**
     * 移除字符串前缀，只移除 prefixes 中第一个匹配到的前缀
     */
    public static String removeFirstPrefixIgnoreCase(CharSequence str, CharSequence... prefixes) {
        return removeFirstPrefix(str, true, prefixes);
    }

    /**
     * 移除字符串前缀，优先移除最长的前缀
     */
    public static String removeMaxPrefix(CharSequence str, boolean ignoreCase, CharSequence... prefixes) {
        if (ArrayUtil.isEmpty(prefixes)) {
            return str.toString();
        }
        return removeFirstPrefix(str, ignoreCase, Arrays.stream(prefixes).sorted(Comparator.comparingInt(CharSequence::length).reversed()).toArray(CharSequence[]::new));
    }

    /**
     * 移除字符串前缀，优先移除最长的前缀
     */
    public static String removeMaxPrefix(CharSequence str, CharSequence... prefixes) {
        return removeMaxPrefix(str, false, prefixes);
    }

    /**
     * 移除字符串前缀，优先移除最长的前缀
     */
    public static String removeAnyPrefixIgnoreCase(CharSequence str, CharSequence... prefixes) {
        return removeMaxPrefix(str, true, prefixes);
    }

    /**
     * 移除字符串前缀，只移除 suffixes 中第一个匹配到的前缀
     */
    public static String removeFirstSuffix(CharSequence str, boolean ignoreCase, CharSequence... suffixes) {
        if (CharSequenceUtil.isNotEmpty(str)) {
            for (CharSequence suffix : suffixes) {
                if (CharSequenceUtil.endWith(str, suffix, ignoreCase)) {
                    return CharSequenceUtil.subPre(str, str.length() - suffix.length());
                }
            }
        }
        return str.toString();
    }

    /**
     * 移除字符串前缀，只移除 suffixes 中第一个匹配到的前缀
     */
    public static String removeFirstSuffix(CharSequence str, CharSequence... suffixes) {
        return removeFirstSuffix(str, false, suffixes);
    }

    /**
     * 移除字符串前缀，只移除 suffixes 中第一个匹配到的前缀
     */
    public static String removeFirstSuffixIgnoreCase(CharSequence str, CharSequence... suffixes) {
        return removeFirstSuffix(str, true, suffixes);
    }

    /**
     * 移除字符串前缀，优先移除最长的前缀
     */
    public static String removeMaxSuffix(CharSequence str, boolean ignoreCase, CharSequence... suffixes) {
        return removeFirstSuffix(str, ignoreCase, Arrays.stream(suffixes).sorted(Comparator.comparingInt(CharSequence::length).reversed()).toArray(CharSequence[]::new));
    }

    /**
     * 移除字符串前缀，优先移除最长的前缀
     */
    public static String removeMaxSuffix(CharSequence str, CharSequence... suffixes) {
        return removeMaxSuffix(str, false, suffixes);
    }

    /**
     * 移除字符串前缀，优先移除最长的前缀
     */
    public static String removeAnySuffixIgnoreCase(CharSequence str, CharSequence... suffixes) {
        return removeMaxSuffix(str, true, suffixes);
    }
}
