package com.github.fanzezhen.fun.framework.core.util;

import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 存储工具类
 *
 * @author fanzezhen
 */
@Slf4j
public class StorageUtil {

    static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+(\\.\\d+)?)\\s*([a-zA-Z]+)");
    // 字节单位常量  
    private static final long BYTE = 1L;
    private static final long KILOBYTE = 1024L * BYTE;
    private static final long MEGABYTE = 1024L * KILOBYTE;
    private static final long GIGABYTE = 1024L * MEGABYTE;
    private static final long TERABYTE = 1024L * GIGABYTE;
    private static final BigDecimal KILOBYTE_BIG_DECIMAL = BigDecimal.valueOf(KILOBYTE);

    /**
     * 将单位为字节的值转为合适单位的展示值
     *
     * @param sizeObj ex：3072
     *
     * @return 3K
     */
    public static String displayBytes(Object sizeObj) {
        if (sizeObj == null) {
            return null;
        }
        long size;
        if (sizeObj instanceof Number sizeNumber) {
            size = sizeNumber.longValue();
        } else {
            String sizeStr = sizeObj.toString();
            if (cn.hutool.core.util.NumberUtil.isLong(sizeStr)) {
                size = NumberUtil.parseLong(sizeStr);
            } else {
                return sizeStr;
            }
        }
        if (size < KILOBYTE) {
            return size + "B";
        }
        // 保留3位有效数字，四舍五入
        BigDecimal figure = BigDecimal.valueOf(size);
        MathContext mathContext = new MathContext(3, RoundingMode.HALF_UP);
        figure = figure.divide(KILOBYTE_BIG_DECIMAL, mathContext);
        if (figure.longValue() < KILOBYTE) {
            return figure.stripTrailingZeros().toPlainString() + "K";
        }
        figure = figure.divide(KILOBYTE_BIG_DECIMAL, mathContext);
        if (figure.longValue() < KILOBYTE) {
            return figure.stripTrailingZeros().toPlainString() + "M";
        }
        figure = figure.divide(KILOBYTE_BIG_DECIMAL, mathContext);
        if (figure.longValue() < KILOBYTE) {
            return figure.stripTrailingZeros().toPlainString() + "G";
        }
        figure = figure.divide(KILOBYTE_BIG_DECIMAL, mathContext);
        if (figure.longValue() < KILOBYTE) {
            return figure.stripTrailingZeros().toPlainString() + "T";
        }
        figure = figure.divide(KILOBYTE_BIG_DECIMAL, mathContext);
        if (figure.longValue() < KILOBYTE) {
            return figure.stripTrailingZeros().toPlainString() + "PB";
        }
        figure = figure.divide(KILOBYTE_BIG_DECIMAL, mathContext);
        if (figure.longValue() < KILOBYTE) {
            return figure.stripTrailingZeros().toPlainString() + "EB";
        }
        figure = figure.divide(KILOBYTE_BIG_DECIMAL, mathContext);
        if (figure.longValue() < KILOBYTE) {
            return figure.stripTrailingZeros().toPlainString() + "ZB";
        }
        figure = figure.divide(KILOBYTE_BIG_DECIMAL, mathContext);
        return figure.stripTrailingZeros().toPlainString() + "YB";
    }

    // 私有方法，用于根据单位返回乘数  
    private static long getMultiplier(String unit) {
        return switch (unit.toLowerCase()) {
            case "b", "byte", "bytes" -> BYTE;
            case "k", "kb", "kib" -> KILOBYTE;
            case "m", "mb", "mib" -> MEGABYTE;
            case "g", "gb", "gib" -> GIGABYTE;
            case "t", "tb", "tib" -> TERABYTE;
            default -> throw new IllegalArgumentException("Invalid unit: " + unit);
        };
    }

    // 将包含单位的字符串转换为字节数  
    public static Long convertToBytes(String sizeWithUnit) {
        // 使用正则表达式匹配数字和单位  
        Matcher matcher = NUMBER_PATTERN.matcher(sizeWithUnit);

        if (matcher.matches()) {
            // 提取数值和单位  
            double size = Double.parseDouble(matcher.group(1));
            String unit = matcher.group(3).toLowerCase();

            // 计算字节数  
            return Math.round(size * getMultiplier(unit));
        } else {
            throw new IllegalArgumentException("Invalid size format: " + sizeWithUnit);
        }
    }

    /**
     * @param input            需要处理的原始字符串
     * @param maxLengthInBytes 字符串截断后的最大允许字节数
     * @param isReverse        是否从字符串的末尾开始截取（如果为 true）或者从开头开始截取（如果为 false）
     */
    public static String truncateStringForBytes(String input, int maxLengthInBytes, boolean isReverse) {
        if (input == null) {
            return null;
        }
        // 获取字符串的字节表示  
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        // 如果字符串已经小于或等于TEXT字段的最大长度，则无需截断  
        if (bytes.length <= maxLengthInBytes) {
            return input;
        }
        StringBuilder stringBuilder = new StringBuilder();
        char[] inputCharArray = input.toCharArray();
        if (isReverse) {
            for (int i = inputCharArray.length - (maxLengthInBytes / 4); i < inputCharArray.length; i++) {
                stringBuilder.append(inputCharArray[i]);
            }
            int byteLength = stringBuilder.toString().getBytes(StandardCharsets.UTF_8).length;
            for (int i = inputCharArray.length - (maxLengthInBytes / 4) - 1; i > 0; i--) {
                byteLength += String.valueOf(inputCharArray[i]).getBytes(StandardCharsets.UTF_8).length;
                if (byteLength > maxLengthInBytes) {
                    break;
                }
                stringBuilder.insert(0, inputCharArray[i]);
            }
        } else {
            for (int i = 0; i <= maxLengthInBytes / 4; i++) {
                stringBuilder.append(inputCharArray[i]);
            }
            int byteLength = stringBuilder.toString().getBytes(StandardCharsets.UTF_8).length;
            for (int i = maxLengthInBytes / 4 + 1; i < inputCharArray.length; i++) {
                byteLength += String.valueOf(inputCharArray[i]).getBytes(StandardCharsets.UTF_8).length;
                if (byteLength > maxLengthInBytes) {
                    break;
                }
                stringBuilder.append(inputCharArray[i]);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * @param input            需要处理的原始字符串
     * @param maxLengthInChars 字符串截断后的最大允许字符数
     * @param filler           填充字符
     * @param isReverse        是否从字符串的末尾开始截取（如果为 true）或者从开头开始截取（如果为 false）
     */
    public static String truncateStringForChars(String input, int maxLengthInChars, char filler, boolean isReverse) {
        if (input == null) {
            return null;
        }
        if (input.length() <= maxLengthInChars) {
            return input;
        }
        return isReverse ?
            (filler + input.substring(input.length() - maxLengthInChars + 1)) :
            (input.substring(0, maxLengthInChars - 1) + filler);
    }

}
