package com.github.fanzezhen.fun.framework.core.log.config;

import com.github.fanzezhen.fun.framework.core.log.support.FunLogHelper;
import lombok.Data;
import org.slf4j.event.Level;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.util.PatternMatchUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Fun Framework 日志的配置属性.
 * <p>
 * 提供日志级别、Web 请求日志、文件路径和模式的配置。
 * 支持动态日志级别匹配以及每个级别的文件大小和保留策略。
 */
@Data
@ConfigurationProperties(prefix = "fun.log")
public class FunLogProperties {

    /**
     * 根日志级别.
     */
    private LogLevel rootLevel = LogLevel.DEBUG;

    /**
     * Web 日志配置.
     */
    private WebLogConfig web;

    /**
     * 日志文件路径.
     */
    private String filePath;

    /**
     * 日志模式表达式.
     */
    private String pattern;

    /**
     * 按日志记录器名称的日志级别映射.
     */
    private Map<String, LogLevel> levelMap = new HashMap<>();

    /**
     * 每个级别的配置映射.
     * <p>
     * 默认配置：
     * TRACE: 每个文件 500MB，总计 5GB，保留 30 天；
     * DEBUG: 每个文件 500MB，总计 2GB，保留 30 天；
     * INFO: 每个文件 500MB，总计 2GB，保留 30 天；
     * WARN: 每个文件 500MB，总计 1GB，保留 90 天；
     * ERROR: 每个文件 500MB，总计 1GB，保留 180 天。
     */
    private Map<String, LevelConfig> levelConfigMap = new HashMap<>();

    /**
     * 设置日志级别映射并初始化日志级别.
     *
     * @param newLevelMap 要设置的级别映射
     */
    public void setLevelMap(final Map<String, LogLevel> newLevelMap) {
        this.levelMap = newLevelMap;
        FunLogHelper.initLogLevel(newLevelMap);
    }

    /**
     * 匹配给定日志记录器名称的日志级别.
     * <p>
     * 首先尝试精确匹配，然后进行模式匹配，最后回退到根级别。
     *
     * @param name 日志记录器名称
     * @return 匹配的日志级别
     */
    public LogLevel matchLevel(final String name) {
        LogLevel logLevel = levelMap.get(name);
        if (logLevel == null) {
            for (Map.Entry<String, LogLevel> entry : levelMap.entrySet()) {
                if (PatternMatchUtils.simpleMatch(entry.getKey(), name)) {
                    logLevel = entry.getValue();
                    break;
                }
            }
        }
        if (logLevel == null) {
            logLevel = rootLevel;
        }
        return logLevel;
    }

    /**
     * Web 日志配置.
     */
    @Data
    public static class WebLogConfig {
        /**
         * 是否开启web入参出参日志，日志阶级别为debug.
         */
        private boolean enable = false;

        /**
         * 日志级别，默认debug.
         */
        private Level level = Level.DEBUG;
    }

    /**
     * 日志级别特定配置.
     */
    @Data
    public static class LevelConfig {

        /**
         * 单个文件最大大小，超过该大小进行文件滚动.
         */
        private String maxFileSize;

        /**
         * 最大保存天数.
         */
        private String maxHistory;

        /**
         * 总大小限制，单位大写 例 1GB  3GB  100MB.
         */
        private String totalSizeCap;
    }

}
