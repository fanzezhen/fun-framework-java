package com.github.fanzezhen.fun.framework.core.log.support;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.alibaba.fastjson2.JSON;
import com.github.fanzezhen.fun.framework.core.log.config.FunLogProperties;
import com.github.fanzezhen.fun.framework.core.log.serializer.IPrintSerializer;
import com.github.fanzezhen.fun.framework.core.model.common.FunFunction;
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
 * 动态日志级别管理助手.
 * <p>
 * 支持在运行时调整特定模块的日志级别，无需重启。
 * <p>
 * 典型场景：
 * - 在生产环境中临时启用 DEBUG 日志以进行故障排查
 * - 按模块使用不同级别的日志（核心模块使用 INFO，辅助模块使用 WARN）
 * - 基于 AOP 的方法入参/出参日志记录，支持动态控制
 * <p>
 * 线程安全：{@link #setLogLevel} 和 {@link #initLogLevel} 使用
 * synchronized 确保写入安全；{@link #getOrGenerateLevelLogger} 使用
 * {@code computeIfAbsent} 确保读取时的单次计算。
 * <p>
 * WeakHashMap 原理：防止长时间运行的应用程序中累积 Logger 对象导致的内存泄漏。
 * 当模块名称不再被引用时，LevelLogger 会被 GC 回收，适用于动态类加载场景。
 */
@Slf4j
@Component
@SuppressWarnings("unused")
public class FunLogHelper {
    /**
     * DEBUG 级别日志记录器.
     */
    private static final LevelLogger DEBUG_LOGGER = log::debug;
    /**
     * INFO 级别日志记录器.
     */
    private static final LevelLogger INFO_LOGGER = log::info;
    /**
     * WARN 级别日志记录器.
     */
    private static final LevelLogger WARN_LOGGER = log::warn;
    /**
     * ERROR 级别日志记录器.
     */
    private static final LevelLogger ERROR_LOGGER = log::error;
    /**
     * TRACE 级别日志记录器.
     */
    private static final LevelLogger TRACE_LOGGER = log::trace;
    /**
     * 默认级别日志记录器.
     */
    private static LevelLogger defaultLevelLogger;
    /**
     * WeakHashMap 防止内存泄漏.
     * <p>
     * 当模块名称不再被引用时，它们的 LevelLogger 会被自动 GC 回收，
     * 避免在动态类加载中累积 Logger。
     */
    static final WeakHashMap<String, LevelLogger> LEVEL_LOGGER_MAP = new WeakHashMap<>();

    /**
     * 打印序列化器列表.
     */
    @Resource
    private List<IPrintSerializer> printSerializerList;
    /**
     * 日志配置属性.
     */
    @Resource
    private FunLogProperties funLogProperties;

    /**
     * 执行函数并记录输入/输出参数.
     *
     * @param module 用于记录日志的模块名称
     * @param invoker 要调用的函数
     * @param requestParam 请求参数
     * @param <T> 输入类型
     * @param <R> 返回类型
     * @return 函数结果
     */
    @SneakyThrows
    public <T, R> R executeByLog(final String module, final FunFunction<T, R> invoker, final T requestParam) {
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


    /**
     * 将多个参数解析为 JSON 字符串.
     *
     * @param args 要解析的参数
     * @return 解析后的 JSON 字符串
     */
    public String resolveArgs(final Object... args) {
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

    /**
     * 使用已注册的序列化器解析单个参数.
     *
     * @param arg 要解析的参数
     * @return 解析后的字符串表示
     */
    public String resolveArg(final Object arg) {
        for (IPrintSerializer argResolve : printSerializerList) {
            if (argResolve.isSupport(arg)) {
                return argResolve.serialize(arg);
            }
        }
        return "'不支持的类型：" + arg.getClass().getName() + "'";
    }

    /**
     * 为给定的日志记录器和级别生成级别日志记录器.
     *
     * @param logger 日志记录器实例
     * @param level 日志级别
     * @return 级别日志记录器
     */
    private static LevelLogger generateLevelLogger(final Logger logger, final LogLevel level) {
        return switch (level) {
            case OFF -> LevelLogger.EMPTY;
            case TRACE -> log.isTraceEnabled() ? logger::trace : LevelLogger.EMPTY;
            case DEBUG -> log.isDebugEnabled() ? logger::debug : LevelLogger.EMPTY;
            case INFO -> log.isInfoEnabled() ? logger::info : LevelLogger.EMPTY;
            case WARN -> log.isWarnEnabled() ? logger::warn : LevelLogger.EMPTY;
            case ERROR, FATAL -> log.isErrorEnabled() ? logger::error : LevelLogger.EMPTY;
        };
    }

    /**
     * 查找给定级别的级别日志记录器.
     *
     * @param level 日志级别
     * @return 级别日志记录器，如果未启用则返回 null
     */
    private static LevelLogger findLevelLogger(final LogLevel level) {
        if (LogLevel.DEBUG.equals(level)) {
            return !log.isDebugEnabled() ? null : DEBUG_LOGGER;
        } else if (LogLevel.INFO.equals(level)) {
            return !log.isInfoEnabled() ? null : INFO_LOGGER;
        } else if (LogLevel.WARN.equals(level)) {
            return !log.isWarnEnabled() ? null : WARN_LOGGER;
        } else if (LogLevel.ERROR.equals(level)) {
            return !log.isErrorEnabled() ? null : ERROR_LOGGER;
        } else if (LogLevel.TRACE.equals(level)) {
            return !log.isTraceEnabled() ? null : TRACE_LOGGER;
        }
        return null;
    }

    /**
     * 根据名称获取日志记录器.
     *
     * @param loggerName 日志记录器名称
     * @return 日志记录器实例
     */
    public static Logger getLogger(final String loggerName) {
        final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        return (Logger) loggerFactory.getLogger(loggerName);
    }

    /**
     * 为给定的日志记录器名称和级别生成级别日志记录器.
     *
     * @param loggerName 日志记录器名称
     * @param level 日志级别
     * @return 级别日志记录器
     */
    public static LevelLogger generateLevelLogger(final String loggerName, final LogLevel level) {
        Logger logger = getLogger(loggerName);
        logger.setLevel(Level.toLevel(level.name()));
        return generateLevelLogger(logger, level);
    }

    /**
     * 设置日志记录器的日志级别.
     *
     * @param loggerName 日志记录器名称
     * @param level 日志级别
     * @return 级别日志记录器
     */
    public static LevelLogger setLogLevel(final String loggerName, final LogLevel level) {
        LevelLogger levelLogger = generateLevelLogger(loggerName, level);
        LEVEL_LOGGER_MAP.put(loggerName, levelLogger);
        return levelLogger;
    }

    /**
     * 从给定的级别映射初始化日志级别.
     *
     * @param levelMap 级别映射
     */
    public static void initLogLevel(final Map<String, LogLevel> levelMap) {
        synchronized (LEVEL_LOGGER_MAP) {
            LEVEL_LOGGER_MAP.clear();
            levelMap.forEach(FunLogHelper::setLogLevel);
        }
    }

    /**
     * 获取给定日志记录器名称的级别日志记录器.
     *
     * @param loggerName 日志记录器名称
     * @return 级别日志记录器，如果未找到则返回 null
     */
    public static LevelLogger getLevelLogger(final String loggerName) {
        return LEVEL_LOGGER_MAP.get(loggerName);
    }

    /**
     * 设置默认级别日志记录器.
     *
     * @param newDefaultLevelLogger 默认级别日志记录器
     */
    private static void setDefaultLevelLogger(final LevelLogger newDefaultLevelLogger) {
        defaultLevelLogger = newDefaultLevelLogger;
    }

    /**
     * 使用配置的级别生成级别日志记录器.
     *
     * @param loggerName 日志记录器名称
     * @return 级别日志记录器
     */
    public LevelLogger generateLevelLogger(final String loggerName) {
        return generateLevelLogger(loggerName, funLogProperties.matchLevel(loggerName));
    }

    /**
     * 获取或生成给定日志记录器名称的级别日志记录器.
     *
     * @param loggerName 日志记录器名称
     * @return 级别日志记录器
     */
    public LevelLogger getOrGenerateLevelLogger(final String loggerName) {
        return LEVEL_LOGGER_MAP.computeIfAbsent(loggerName, this::generateLevelLogger);
    }

    /**
     * 检查给定日志记录器名称的日志记录是否已禁用.
     *
     * @param loggerName 日志记录器名称
     * @return 如果日志记录已禁用则返回 true，否则返回 false
     */
    public boolean isDisabled(final String loggerName) {
        return LevelLogger.EMPTY.equals(getOrGenerateLevelLogger(loggerName));
    }

    /**
     * 在 bean 构造后初始化默认级别日志记录器.
     */
    @PostConstruct
    public void init() {
        setDefaultLevelLogger(findLevelLogger(funLogProperties.getRootLevel()));
    }
}
