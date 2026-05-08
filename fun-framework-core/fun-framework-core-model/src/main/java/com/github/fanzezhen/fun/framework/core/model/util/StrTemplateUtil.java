package com.github.fanzezhen.fun.framework.core.model.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.model.constant.RegexConstant;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * 字符串模板工具类
 * <p>
 * 支持解析 ${varName} 和 '${varName}' 两种格式的变量占位符，
 * 从提供的变量Map中替换为实际值。
 * <p>
 * <b>使用场景：</b>配置文件模板解析、SQL动态参数替换、日志模板格式化等
 *
 */
@Slf4j
public class StrTemplateUtil {

    /**
     * 格式化模板字符串，替换其中的变量占位符
     * <p>
     * 支持 ${varName} 和 '${varName}' 两种格式。
     * 当变量不存在时，根据nullToEmpty参数决定是替换为空字符串还是保留占位符。
     * <p>
     * <b>性能考虑：</b>使用预编译的正则Pattern进行匹配，适合批量模板处理
     *
     * @param template          模板字符串
     * @param nullToEmpty       变量不存在时是否替换为空字符串（false则保留占位符）
     * @param envVarMap         变量Map
     * @param defaultProperties 默认变量键值对（奇数位为key，偶数位为value）
     * @return 替换后的字符串
     */
    public static String format(String template, boolean nullToEmpty, Map<String, Object> envVarMap, String... defaultProperties) {
        envVarMap = getVarMapOrEmpty(envVarMap, defaultProperties);
        Matcher matcher = RegexConstant.PATTERN_$_CURLY_BRACKET_COMPATIBLE_APOSTROPHE.matcher(template);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String k = matcher.group(2);
            if (CharSequenceUtil.isEmpty(k)) {
                k = matcher.group(1);
            }
            Object v = envVarMap.get(k);
            if (v == null) {
                if (k != null && nullToEmpty) {
                    log.warn("变量{}未配置", k);
                    v = CharSequenceUtil.EMPTY;
                } else {
                    continue;
                }
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(v.toString()));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 合并变量Map和默认属性
     * <p>
     * defaultProperties格式：key1, value1, key2, value2...
     * 只在varMap中不存在对应key时才添加默认值（putIfAbsent语义）
     *
     * @param varMap            变量Map
     * @param defaultProperties 默认属性数组（偶数索引为key，奇数索引为value）
     * @return 合并后的Map，如果输入为null则返回空Map
     */
    public static Map<String, Object> getVarMapOrEmpty(Map<String, Object> varMap, String... defaultProperties) {
        if (defaultProperties != null) {
            if (varMap == null) {
                varMap = new HashMap<>(defaultProperties.length / 2, 1f);
            }
            for (int i = 1; i < defaultProperties.length; i = i + 2) {
                varMap.putIfAbsent(defaultProperties[i - 1], defaultProperties[i]);
            }
        }
        return varMap != null ? varMap : MapUtil.empty();
    }
    
    private StrTemplateUtil() {
    }
}
