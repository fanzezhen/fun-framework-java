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
 *
 */
@Slf4j
public class StrTemplateUtil {

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
