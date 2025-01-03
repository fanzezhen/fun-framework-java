package com.github.fanzezhen.fun.framework.core.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;

/**
 * @author zezhen.fan
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
        int result = 0;
        // 存放一个单位的数字如：十万
        int temp = 1;
        // 判断是否有chArr
        int count = 0;
        char[] cnArr = new char[]{'一', '二', '三', '四', '五', '六', '七', '八', '九'};
        char[] chArr = new char[]{'十', '百', '千', '万', '亿'};
        for (int i = 0; i < chineseNumber.length(); i++) {
            // 判断是否是chArr
            boolean b = true;
            char c = chineseNumber.charAt(i);
            // 非单位，即数字
            for (int j = 0; j < cnArr.length; j++) {
                if (c == cnArr[j]) {
                    // 添加下一个单位之前，先把上一个单位值添加到结果中
                    if (0 != count) {
                        result += temp;
                        count = 0;
                    }
                    // 下标+1，就是对应的值
                    temp = j + 1;
                    b = false;
                    break;
                }
            }
            // 单位
            if (b) {
                for (int j = 0; j < chArr.length; j++) {
                    if (c == chArr[j]) {
                        switch (j) {
                            case 0:
                                temp *= 10;
                                break;
                            case 1:
                                temp *= 100;
                                break;
                            case 2:
                                temp *= 1000;
                                break;
                            case 3:
                                temp *= 10000;
                                break;
                            case 4:
                                temp *= 100000000;
                                break;
                            default:
                                break;
                        }
                        count++;
                    }
                }
            }
            // 遍历到最后一个字符
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
        } else if (decimalStr.length() == 2) {
            intStr = intStr + decimalStr;
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
