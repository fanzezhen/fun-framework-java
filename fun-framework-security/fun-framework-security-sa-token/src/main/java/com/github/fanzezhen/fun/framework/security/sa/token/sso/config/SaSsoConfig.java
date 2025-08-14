package com.github.fanzezhen.fun.framework.security.sa.token.sso.config;


import cn.dev33.satoken.util.SaFoxUtil;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.function.BiFunction;

/**
 * Sa-Token SSO 单点登录模块 配置类 Model
 */
@Data
public class SaSsoConfig implements Serializable {
    /**
     * 1秒 = 1000毫秒
     */
    private static final long MILL_SECONDS = 1000L;

    /**
     * 1分钟 = 60秒
     */
    private static final long SECONDS = 60L;
    /**
     * 十分钟
     */
    private static final long TEN = 10L;
    /**
     * SSO-Server端：未登录时返回的View
     */
//    private Supplier<LoginBO> notLoginView = LoginBO::new;
    /**
     * SSO-Server端：登录函数
     */
//    private Function<UnifyLoginHandleBo, LoginBO> doLoginHandle = ssoHandleBo -> null;
    /**
     * SSO-Client端：自定义校验Ticket返回值的处理逻辑 （每次从认证中心获取校验Ticket的结果后调用）
     * <p>
     * 参数：loginId, back
     * <p>
     * 返回值：返回给前端的值
     */
    private BiFunction<Object, String, Object> ticketResultHandle = null;
    /**
     * 当前 Client 名称标识，用于和 ticket 码的互相锁定
     */
    private String client;
    /**
     * 配置 Server 端单点登录授权地址
     */
    private String authUrl = "/sso/auth";
    /**
     * 是否打开单点注销功能
     */
    private Boolean isSlo = true;
    /**
     * 是否打开模式三（此值为 true 时将使用 http 请求：校验ticket值、单点注销、获取userinfo）
     */
    private Boolean isHttp = false;
    /**
     * 接口调用秘钥 (用于SSO模式三单点注销的接口通信身份校验)
     */
    private String secretKey;
    /**
     * 配置 Server 端的 ticket 校验地址
     */
    private String checkTicketUrl = "/auth/verify-access-token";

    // ----------------- 其它
    /**
     * 配置 Server 端查询 userinfo 地址
     */
    private String userinfoUrl = "/sso/userinfo";
    /**
     * 配置 Server 端单点注销地址
     */
    private String sloUrl = "/sso/signout";
    /**
     * 配置当前 Client 端的单点注销回调URL （为空时自动获取）
     */
    private String ssoLogoutCall;

    /**
     * SSO-退出回调地址
     */
    private String logOutUrl;

    /**
     * 配置 Server 端主机总地址，拼接在 authUrl、checkTicketUrl、userinfoUrl、sloUrl 属性前面，用以简化各种 url 配置
     */
    private String serverUrl;

    /**
     * 解析规则路径
     */
    private String jsonPath;

    /**
     * 接口调用时的时间戳允许的差距（单位：ms），-1代表不校验差距
     */
    private long timestampDisparity = MILL_SECONDS * SECONDS * TEN;

    /**
     * @return 是否打开单点注销功能
     */
    public boolean isSlo() {
        return Boolean.TRUE.equals(isSlo);
    }

    /**
     * @return 获取拼接url：Server 端单点登录授权地址
     */
    public String splicingAuthUrl() {
        return SaFoxUtil.spliceTwoUrl(getServerUrl(), getAuthUrl());
    }

    /**
     * @return 获取拼接url：Server 端的 ticket 校验地址
     */
    public String splicingCheckTicketUrl() {
        return SaFoxUtil.spliceTwoUrl(getServerUrl(), getCheckTicketUrl());
    }

    /**
     * @return 获取拼接url：Server 端查询 userinfo 地址
     */
    public String splicingUserinfoUrl() {
        return SaFoxUtil.spliceTwoUrl(getServerUrl(), getUserinfoUrl());
    }

    /**
     * @return 获取拼接url：Server 端单点注销地址
     */
    public String splicingSloUrl() {
        return SaFoxUtil.spliceTwoUrl(getServerUrl(), getSloUrl());
    }

}
