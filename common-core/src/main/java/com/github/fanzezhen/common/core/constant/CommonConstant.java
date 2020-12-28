package com.github.fanzezhen.common.core.constant;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zezhen.fan
 */
public class CommonConstant {
    /**
     * 项目目录
     */
    public static final String PROJECT_PATH = System.getProperty("user.dir") + File.separator;
    /**
     * 临时文件夹
     */
    public static final String TMP_FOLDER = "tmp";
    /**
     * 临时文件夹全路径
     */
    public static final String TMP_PATH = PROJECT_PATH + TMP_FOLDER + File.separator;
    /**
     * 默认密码
     */
    public static final String DEFAULT_USER_PASSWORD = "111111";
    /**
     * 分隔符
     */
    public static final String SEPARATOR = "/";
    /**
     * 编码格式
     */
    public static final String CODED_FORMAT = "UTF-8";

    public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    public static final String MINIMALISM_DATE_DATE_PATTERN = "yyyyMMdd";
    public static final String DEFAULT_DATE_HYPHEN = "-";

    /**
     * 登录失败时，用来判断是返回json数据还是跳转html页面
     */
    public static final boolean LOGIN_FAILED_RETURN_JSON = true;
    /**
     * 登录失败时，用来判断是返回json数据还是跳转html页面
     */
    public static final boolean LOGIN_SUCCESS_RETURN_JSON = true;

    public static final String LOGIN_FAILED_MESSAGE = "登录失败！";
    public static final String ADMIN_ADDRESS_PATTERN = "/admin/**";
    public static final String OAUTH_ADDRESS_PATTERN = "/oauth/**";
    public static final String TOKEN_KEY = "x-token";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String PERMISSION_PREFIX = "PERMISSION_";
    public static final String PERMISSION_DEFAULT_PID = "0";

    public static final ThreadPoolExecutor SYS_EXECUTOR = new ThreadPoolExecutor(
            10, 15, 10, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());
}
