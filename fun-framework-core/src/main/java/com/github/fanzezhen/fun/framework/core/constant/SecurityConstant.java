package com.github.fanzezhen.fun.framework.core.constant;

import cn.hutool.core.util.ArrayUtil;

import java.io.File;

/**
 * @author zezhen.fan
 */
public interface SecurityConstant {
    String HOME_ADDRESS = "/hello";
    String LOGIN_ADDRESS = "/oauth/login";
    String DEFAULT_LOGIN_API = "/login";
    /**
     * 异常接口匹配格式
     */
    String ERROR_MATCHERS = "/error/**";
    /**
     * 日志接口匹配格式
     */
    String LOG_MATCHERS = "/log/**";
    /**
     * 资源接口匹配格式
     */
    String OAUTH_MATCHERS = "/oauth/**";
    String ADMIN_ADDRESS_PATTERN = "/admin/**";
    /**
     * 开放接口匹配格式
     */
    String PUBLIC_MATCHERS = "/public/**";
    /**
     * 静态文件接口匹配格式
     */
    String STATIC_MATCHERS = "/static/**";
    /**
     * SWAGGER-UI接口匹配格式
     */
    String SWAGGER_UI_MATCHERS = "/swagger-ui/**";
    /**
     * SWAGGER-RESOURCE接口匹配格式
     */
    String SWAGGER_RESOURCE_MATCHERS = "/swagger-resources/**";
    /**
     * SWAGGER-API-DOC接口匹配格式
     */
    String SWAGGER_API_DOCS_MATCHERS = "/v3/api-docs/**";
    /**
     * SWAGGER接口匹配格式集合
     */
    String[] SWAGGER_MATCHERS = {SWAGGER_UI_MATCHERS, SWAGGER_RESOURCE_MATCHERS, SWAGGER_API_DOCS_MATCHERS};

    /**
     * 权限加载的前缀
     */
    String PERMISSION_PREFIX = "permission_";

    /**
     * 角色加载的前缀
     */
    String ROLE_PREFIX = "ROLE_";

    /**
     * 验证码
     */
    String SESSION_KEY_CAPTCHA = "SESSION_KEY_CAPTCHA";

    /**
     * 用户对应在session中key
     */
    String USER_SESSION_KEY = "SECURITY_USER_ATTR";

    /**
     * 角色对应在session中的key
     */
    String ROLE_SESSION_KEY = "SECURITY_ROLE_ATTR";

    /**
     * 权限对应在session中的key
     */
    String PERMISSION_SESSION_KEY = "SECURITY_PERMISSION_ATTR";
    /**
     * 项目目录
     */
    String PROJECT_PATH = System.getProperty("user.dir") + File.separator;
    /**
     * 临时文件夹
     */
    String TMP_FOLDER = "tmp";
    /**
     * 临时文件夹全路径
     */
    String TMP_PATH = PROJECT_PATH + TMP_FOLDER + File.separator;
    /**
     * 默认密码
     */
    String DEFAULT_USER_PASSWORD = "111111";
    /**
     * 路径分隔符
     */
    String SEPARATOR_DIR = "/";
    /**
     * URL分隔符
     */
    String SEPARATOR_URL = "?";
    /**
     * 编码格式
     */
    String CODED_FORMAT = "UTF-8";

    String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    String DEFAULT_DATE_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";
    String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    String MINIMALISM_DATE_DATE_PATTERN = "yyyyMMdd";
    String DEFAULT_DATE_HYPHEN = "-";

    /**
     * 登录失败时，用来判断是返回json数据还是跳转html页面
     */
    boolean LOGIN_FAILED_RETURN_JSON = true;
    /**
     * 登录失败时，用来判断是返回json数据还是跳转html页面
     */
    boolean LOGIN_SUCCESS_RETURN_JSON = true;

    String LOGIN_FAILED_MESSAGE = "登录失败！";
    String OAUTH_ADDRESS_PATTERN = "/oauth/**";
    String PERMISSION_DEFAULT_PID = "0";

    /**
     * 不校验权限的接口
     */
    String[] IGNORING_ANT_MATCHERS = ArrayUtil.addAll(SWAGGER_MATCHERS,
            new String[]{ERROR_MATCHERS, LOGIN_ADDRESS, LOG_MATCHERS, OAUTH_MATCHERS, PUBLIC_MATCHERS, STATIC_MATCHERS});

    /**
     * 忽略CSRF的接口
     */
    String[] CSRF_IGNORING_ANT_MATCHERS = {"/api/**", LOG_MATCHERS, PUBLIC_MATCHERS};
}
