package com.github.fanzezhen.fun.framework.core.log.base.support;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.alibaba.fastjson2.JSON;
import com.github.fanzezhen.fun.framework.core.log.base.config.FunLogProperties;
import com.github.fanzezhen.fun.framework.core.log.base.serializer.IPrintSerializer;
import com.github.fanzezhen.fun.framework.core.model.FunFunction;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 *
 */
@Slf4j
@Component
@SuppressWarnings("unused")
public class FunLogHelper {
    private static final LevelLogger debugLogger = log::debug;
    private static final LevelLogger infoLogger = log::info;
    private static final LevelLogger warnLogger = log::warn;
    private static final LevelLogger errorLogger = log::error;
    private static final LevelLogger traceLogger = log::trace;
    private static LevelLogger defaultLevelLogger;
    static final WeakHashMap<String, LevelLogger> levelLoggerMap = new WeakHashMap<>();

    @Resource
    private List<IPrintSerializer> printSerializerList;
    @Resource
    private FunLogProperties funLogProperties;

    /**
     * 执行并打印日志
     */
    @SneakyThrows
    public <T, R> R executeByLog(String module, FunFunction<T, R> invoker, T requestParam) {
        long startTime = System.currentTimeMillis();
        LevelLogger logger = null;
        try {
            logger = getOrGenerateLevelLogger(module);
        } catch (Exception e) {
            defaultLevelLogger.log("", e);
        }
        if (logger == null) {
            logger = defaultLevelLogger;
        }
        try {
            logger.log("入参：{}", resolveArg(requestParam));
        } catch (Exception e) {
            logger.log("", e);
        }
        R response = invoker.call(requestParam);
        try {
            logger.log("出参：{}", resolveArg(response));
        } catch (Exception e) {
            logger.log("", e);
        }
        try {
            log.info("耗时：{} ms", System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            logger.log("", e);
        }
        return response;
    }


    public String resolveArgs(Object... args) {
        if (Objects.isNull(args) || args.length == 0) {
            return "";
        }
        if (args.length == 1) {
            return resolveArg(args[0]);
        } else {
            String[] strArr = new String[args.length];
            for (int i = 0; i < args.length; i++) {
                strArr[i] = resolveArg(args[i]);
            }
            return JSON.toJSONString(strArr);
        }
    }


    public String resolveArg(Object arg) {
        for (IPrintSerializer argResolve : printSerializerList) {
            if (argResolve.isSupport(arg)) {
                return argResolve.serialize(arg);
            }
        }
        return "'不支持的类型：" + arg.getClass().getName() + "'";
    }

    private static LevelLogger generateLevelLogger(Logger logger, LogLevel level) {
        return switch (level) {
            case OFF -> LevelLogger.EMPTY;
            case TRACE -> log.isTraceEnabled() ? logger::trace : LevelLogger.EMPTY;
            case DEBUG -> log.isDebugEnabled() ? logger::debug : LevelLogger.EMPTY;
            case INFO -> log.isInfoEnabled() ? logger::info : LevelLogger.EMPTY;
            case WARN -> log.isWarnEnabled() ? logger::warn : LevelLogger.EMPTY;
            case ERROR, FATAL -> log.isErrorEnabled() ? logger::error : LevelLogger.EMPTY;
        };
    }

    private static LevelLogger findLevelLogger(LogLevel level) {
        if (LogLevel.DEBUG.equals(level)) {
            return !log.isDebugEnabled() ? null : debugLogger;
        } else if (LogLevel.INFO.equals(level)) {
            return !log.isInfoEnabled() ? null : infoLogger;
        } else if (LogLevel.WARN.equals(level)) {
            return !log.isWarnEnabled() ? null : warnLogger;
        } else if (LogLevel.ERROR.equals(level)) {
            return !log.isErrorEnabled() ? null : errorLogger;
        } else if (LogLevel.TRACE.equals(level)) {
            return !log.isTraceEnabled() ? null : traceLogger;
        }
        return null;
    }

    public static Logger getLogger(String loggerName) {
        final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        return (Logger) loggerFactory.getLogger(loggerName);
    }

    public static LevelLogger generateLevelLogger(String loggerName, LogLevel level) {
        Logger logger = getLogger(loggerName);
        logger.setLevel(Level.toLevel(level.name()));
        return generateLevelLogger(logger, level);
    }

    public static LevelLogger setLogLevel(String loggerName, LogLevel level) {
        LevelLogger levelLogger = generateLevelLogger(loggerName, level);
        levelLoggerMap.put(loggerName, levelLogger);
        return levelLogger;
    }
    
    

    public static void initLogLevel(Map<String, LogLevel> levelMap) {
        synchronized (levelLoggerMap) {
            levelLoggerMap.clear();
            levelMap.forEach(FunLogHelper::setLogLevel);
        }
    }

    public static LevelLogger getLevelLogger(String loggerName) {
        return levelLoggerMap.get(loggerName);
    }

    private static void setDefaultLevelLogger(LevelLogger defaultLevelLogger) {
        FunLogHelper.defaultLevelLogger = defaultLevelLogger;
    }

    public LevelLogger generateLevelLogger(String loggerName) {
        return generateLevelLogger(loggerName, funLogProperties.matchLevel(loggerName));
    }

    public LevelLogger getOrGenerateLevelLogger(String loggerName) {
        return levelLoggerMap.computeIfAbsent(loggerName, this::generateLevelLogger);
    }

    public boolean isDisabled(String loggerName) {
        return LevelLogger.EMPTY.equals(getOrGenerateLevelLogger(loggerName));
    }

    @PostConstruct
    public void init() {
        setDefaultLevelLogger(findLevelLogger(funLogProperties.getRootLevel()));
    }
}
