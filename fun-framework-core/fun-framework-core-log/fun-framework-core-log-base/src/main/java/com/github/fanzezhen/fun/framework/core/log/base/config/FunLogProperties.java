package com.github.fanzezhen.fun.framework.core.log.base.config;

import com.github.fanzezhen.fun.framework.core.log.base.support.FunLogHelper;
import lombok.Data;
import org.slf4j.event.Level;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.util.PatternMatchUtils;

import java.util.HashMap;
import java.util.Map;

/**
 */
@Data
@ConfigurationProperties(prefix = "fun.log")
public class FunLogProperties {

    /**
     * 是否开启web入参出参日志，日志阶级别为debug
     */
    private LogLevel rootLevel = LogLevel.DEBUG;

    /**
     * 是否开启web入参出参日志，日志阶级别为debug
     */
    private WebLogConfig web;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 日志表达式
     */
    private String pattern;

    /**
     * 日志级别
     */
    private Map<String, LogLevel> levelMap = new HashMap<>();

    /**
     * 各级别配置
     * <p>
     * TRACE 单个文件500MB，总大小5GB，保留天数30天
     * <br/>
     * DEBUG 单个文件500MB，总大小2GB，保留天数30天
     * <br/>
     * INFO 单个文件500MB，总大小2GB，保留天数30天
     * <br/>
     * WARN 单个文件500MB，总大小1GB，保留天数90天
     * <br/>
     * ERROR 单个文件500MB，总大小1GB，保留天数180天
     */
    private Map<String, LevelConfig> levelConfigMap = new HashMap<>();

    public void setLevelMap(Map<String, LogLevel> levelMap) {
        this.levelMap = levelMap;
        FunLogHelper.initLogLevel(levelMap);
    }

    public LogLevel matchLevel(String name) {
        LogLevel logLevel = levelMap.get(name);
        if (logLevel==null){
            for (Map.Entry<String, LogLevel> entry : levelMap.entrySet()) {
                if (PatternMatchUtils.simpleMatch(entry.getKey(), name)) {
                    logLevel = entry.getValue();
                    break;
                }
            }
        }
        if (logLevel == null){
            logLevel = rootLevel;
        }
        return logLevel;
    }

    @Data
    public static class WebLogConfig {
        /**
         * 是否开启web入参出参日志，日志阶级别为debug
         */
        private boolean enable = false;

        /**
         * 日志级别，默认debug
         */
        private Level level = Level.DEBUG;
    }

    @Data
    public static class LevelConfig {

        /**
         * 单个文件最大大小，超过该大小进行文件滚动，
         */
        private String maxFileSize;

        /**
         * 最大保存天数
         */
        private String maxHistory;

        /**
         * 总大小限制，单位大写 例 1GB  3GB  100MB
         */
        private String totalSizeCap;
    }

}
