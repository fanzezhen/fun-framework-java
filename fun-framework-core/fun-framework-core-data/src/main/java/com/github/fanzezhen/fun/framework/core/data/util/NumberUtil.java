package com.github.fanzezhen.fun.framework.core.data.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fanzezhen
 */
public class NumberUtil {
    private NumberUtil() {
    }

    static final String CHINESE_TEN = "一十";
    static final String CHINESE_TEN_ZERO = "一十零";

    /**
     * 阿拉伯数字转中文数字
     *
     * @param string 数字字符串
     * @return 汉字数字字符串
     */
    public static String numToChinese(String string) {
        String[] s1 = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] s2 = {"十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千"};
        StringBuilder result = new StringBuilder();
        int n = string.length();
        for (int i = 0; i < n; i++) {
            int num = string.charAt(i) - '0';
            if (i != n - 1 && num != 0) {
                result.append(s1[num]).append(s2[n - 2 - i]);
            } else {
                result.append(s1[num]);
            }
        }
        if (result.indexOf(CHINESE_TEN_ZERO) == 0) {
            return result.substring(1, 2);
        } else if (result.indexOf(CHINESE_TEN) == 0) {
            return result.substring(1);
        }
        return result.toString();
    }

    /**
     * 中文數字转阿拉伯数组【十万九千零六十  --> 109060】
     *
     * @param chineseNumber 中文數字
     * @return 阿拉伯数
     */
    @SuppressWarnings("unused")
    public static Integer chineseNumberToInteger(String chineseNumber) {
        if (CharSequenceUtil.isEmpty(chineseNumber)) {
            return null;
        }

        Map<Character, Integer> numberMap = new HashMap<>();
        numberMap.put('一', 1);
        numberMap.put('二', 2);
        numberMap.put('三', 3);
        numberMap.put('四', 4);
        numberMap.put('五', 5);
        numberMap.put('六', 6);
        numberMap.put('七', 7);
        numberMap.put('八', 8);
        numberMap.put('九', 9);

        Map<Character, Integer> unitMap = new HashMap<>();
        unitMap.put('十', 10);
        unitMap.put('百', 100);
        unitMap.put('千', 1000);
        unitMap.put('万', 10000);
        unitMap.put('亿', 100000000);

        int result = 0;
        int temp = 1;
        int count = 0;

        for (int i = 0; i < chineseNumber.length(); i++) {
            char c = chineseNumber.charAt(i);

            if (numberMap.containsKey(c)) {
                if (count != 0) {
                    result += temp;
                    count = 0;
                }
                temp = numberMap.get(c);
            } else if (unitMap.containsKey(c)) {
                temp *= unitMap.get(c);
                count++;
            }

            if (i == chineseNumber.length() - 1) {
                result += temp;
            }
        }

        return result;
    }

    /**
     * 转百分数
     */
    public static String toPercent(String numStr) {
        if (numStr == null) {
            return null;
        }
        numStr = numStr.trim();
        if (CharSequenceUtil.isEmpty(numStr)) {
            return numStr;
        }
        String[] strings = numStr.split("\\.");
        if (strings.length > 2) {
            return numStr;
        }
        if (strings.length == 1 || strings[1].isEmpty()) {
            return "0".equals(strings[0]) ? "0" : strings[0] + "00%";
        }
        String intStr = strings[0];
        String decimalStr = strings[1];
        if (decimalStr.length() == 1) {
            intStr = intStr + decimalStr + "0";
            decimalStr = CharSequenceUtil.EMPTY;
        } else if (decimalStr.length() == 2) {
            intStr = intStr + decimalStr;
            decimalStr = CharSequenceUtil.EMPTY;
        } else {
            intStr = intStr + decimalStr.substring(0, 2);
            decimalStr = decimalStr.substring(2);
        }
        intStr = intStr.replaceAll("^0+", "");
        if (intStr.isEmpty()) {
            intStr = "0";
        }
        decimalStr = decimalStr.replaceAll("0+$", "");
        return decimalStr.isEmpty() ? (intStr + "%") : intStr + StrPool.DOT + decimalStr + "%";
    }
}
