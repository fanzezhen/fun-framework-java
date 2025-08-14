package com.github.fanzezhen.fun.framework.security.sa.token.sso;

/**
 * Sa-Token-SSO模块相关常量
 */
public final class SaSsoConstant {


    /**
     * Client端单点注销回调URL的Set集合，存储在Session中使用的key
     */
    public static final String SLO_CALLBACK_SET_KEY = "SLO_CALLBACK_SET_KEY_";
    /**
     * 表示OK的返回结果
     */
    public static final String OK = "ok";
    /**
     * 表示自己
     */
    public static final String SELF = "self";
    /**
     * 表示简单模式（SSO模式一）
     */
    public static final String MODE_SIMPLE = "simple";
    /**
     * 表示ticket模式（SSO模式二和模式三）
     */
    public static final String MODE_TICKET = "ticket";
    /**
     * 表示请求没有得到任何有效处理 {msg: "not handle"}
     */
    public static final String NOT_HANDLE = "{\"msg\": \"not handle\"}";

    /**
     * 私有化构造函数
     */
    private SaSsoConstant() {
    }

}
