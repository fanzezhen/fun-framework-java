package com.github.fanzezhen.fun.framework.core.model.util;

import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 存储单位转换工具类
 * <p>
 * 提供字节数与人类可读格式（B/K/M/G/T等）之间的相互转换，
 * 以及字符串按字节数/字符数截断的功能。
 * <p>
 * <b>使用场景：</b>文件大小展示、数据库TEXT字段长度限制、日志内容截断
 */
@Slf4j
public class StorageUtil {
    /**
     * 工具类不允许实例化
     */
    private StorageUtil() {
    }

    /**
     * 数字单位正则表达式
     */
    static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+(\\.\\d+)?)\\s*([a-zA-Z]+)");

    /**
     * 字节单位：1 Byte
     */
    private static final long BYTE = 1L;

    /**
     * 字节单位：1 KB = 1024 Bytes
     */
    private static final long KILOBYTE = 1024L * BYTE;

    /**
     * 字节单位：1 MB = 1024 KB
     */
    private static final long MEGABYTE = 1024L * KILOBYTE;

    /**
     * 字节单位：1 GB = 1024 MB
     */
    private static final long GIGABYTE = 1024L * MEGABYTE;

    /**
     * 字节单位：1 TB = 1024 GB
     */
    private static final long TERABYTE = 1024L * GIGABYTE;

    /**
     * KB 单位的 BigDecimal 表示
     */
    private static final BigDecimal KILOBYTE_BIG_DECIMAL = BigDecimal.valueOf(KILOBYTE);

    /**
     * 将字节数转换为人类可读的存储单位格式
     * <p>
     * 自动选择合适的单位（B/K/M/G/T/PB/EB/ZB/YB），保留3位有效数字
     * </p>
     *
     * @param sizeObj 字节数（可以是数字或字符串），例如 3072
     * @return 人类可读格式，例如 "3K"，如果输入为 null 则返回 null
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
        MathContext mathContext = new MathContext(4, RoundingMode.HALF_UP);
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

    /**
     * 根据存储单位获取对应的字节数乘数
     *
     * @param unit 存储单位（如 "b"/"k"/"m"/"g"/"t"，不区分大小写）
     * @return 对应的字节数乘数
     * @throws IllegalArgumentException 如果单位不支持
     */
    public static long getMultiplier(String unit) {
        return switch (unit.toLowerCase()) {
            case "b", "byte", "bytes" -> BYTE;
            case "k", "kb", "kib" -> KILOBYTE;
            case "m", "mb", "mib" -> MEGABYTE;
            case "g", "gb", "gib" -> GIGABYTE;
            case "t", "tb", "tib" -> TERABYTE;
            default -> throw new IllegalArgumentException("Invalid unit: " + unit);
        };
    }

    /**
     * 将带单位的存储大小字符串转换为字节数
     * <p>
     * 支持格式：数字 + 单位，如 "1.5M"、"2G"、"500K"
     * </p>
     *
     * @param sizeWithUnit 带单位的存储大小字符串
     * @return 字节数
     * @throws IllegalArgumentException 如果格式不正确或单位不支持
     */
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
     * 向 StringBuilder 前端插入字符（按字节数限制）
     * <p>
     * 从字符数组的末尾开始，向 StringBuilder 前端插入字符，直到达到字节数限制
     * </p>
     *
     * @param stringBuilder    StringBuilder 对象
     * @param maxLengthInBytes 最大允许字节数
     * @param inputCharArray   输入字符数组
     */
    public static void insertStringBuilder(StringBuilder stringBuilder, int maxLengthInBytes, char[] inputCharArray) {
        int byteLength = stringBuilder.toString().getBytes(StandardCharsets.UTF_8).length;
        for (int i = inputCharArray.length - (maxLengthInBytes / 4) - 1; i > 0; i--) {
            byteLength += String.valueOf(inputCharArray[i]).getBytes(StandardCharsets.UTF_8).length;
            if (byteLength > maxLengthInBytes) {
                break;
            }
            stringBuilder.insert(0, inputCharArray[i]);
        }
    }

    /**
     * 根据字节数截断字符串
     *
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
            insertStringBuilder(stringBuilder, maxLengthInBytes, inputCharArray);
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
     * 根据字符数截断字符串
     * <p>
     * 当字符串长度超过限制时，截断并在首尾添加填充字符（如省略号）
     * </p>
     *
     * @param input            需要处理的原始字符串
     * @param maxLengthInChars 最大允许字符数
     * @param filler           填充字符（通常为省略号）
     * @param isReverse        true表示保留末尾字符，false表示保留开头字符
     * @return 截断后的字符串，如果输入为null则返回null
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
