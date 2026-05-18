package com.github.fanzezhen.fun.framework.security.sa.token.sso.config;


import cn.dev33.satoken.util.SaFoxUtil;
import com.github.fanzezhen.fun.framework.core.model.constant.NormalTypeConstant;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.function.BiFunction;

/**
 * Sa-Token SSO 单点登录模块配置类。
 * <p>
 * 提供 SSO 客户端和服务端的配置参数。
 */
@Data
public class SaSsoConfig implements Serializable {
    /**
     * 十分钟（单位：分钟）。
     */
    private static final long TEN_MINUTES = 10L;
    /**
     * SSO Client端校验 Ticket 返回值的处理逻辑。
     * <p>
     * 每次从认证中心获取校验 Ticket 的结果后调用。
     * 参数：loginId, back；返回值：返回给前端的值。
     */
    private transient BiFunction<Object, String, Object> ticketResultHandle = null;

    /**
     * 当前 Client 名称标识。
     * <p>
     * 用于和 Ticket 码的互相锁定。
     */
    private String client;

    /**
     * Server 端单点登录授权地址。
     */
    private String authUrl = "/sso/auth";

    /**
     * 是否打开单点注销功能。
     */
    private Boolean isSlo = true;

    /**
     * 是否打开模式三。
     * <p>
     * 此值为 true 时将使用 HTTP 请求：校验 Ticket 值、单点注销、获取 userinfo。
     */
    private Boolean isHttp = false;

    /**
     * 接口调用密钥。
     * <p>
     * 用于 SSO 模式三单点注销的接口通信身份校验。
     */
    private String secretKey;

    /**
     * Server 端的 Ticket 校验地址。
     */
    private String checkTicketUrl = "/auth/verify-access-token";

    // ----------------- 其它配置 -------------------

    /**
     * Server 端查询 userinfo 地址。
     */
    private String userinfoUrl = "/sso/userinfo";

    /**
     * Server 端单点注销地址。
     */
    private String sloUrl = "/sso/signout";

    /**
     * 当前 Client 端的单点注销回调URL。
     * <p>
     * 为空时自动获取。
     */
    private String ssoLogoutCall;

    /**
     * SSO 退出回调地址。
     */
    private String logOutUrl;

    /**
     * Server 端主机总地址。
     * <p>
     * 拼接在 authUrl、checkTicketUrl、userinfoUrl、sloUrl 属性前面，用以简化各种 URL 配置。
     */
    private String serverUrl;

    /**
     * JSON 解析规则路径。
     */
    private String jsonPath;

    /**
     * 接口调用时的时间戳允许的差距（单位：毫秒）。
     * <p>
     * -1 代表不校验差距，默认为 10 分钟。
     */
    private long timestampDisparity = NormalTypeConstant.INT_MILLIS_PER_SECOND * NormalTypeConstant.INT_ONE_MINUTE_SECONDS * TEN_MINUTES;

    /**
     * 判断是否打开单点注销功能。
     *
     * @return true 表示已打开，false 表示未打开
     */
    public boolean isSlo() {
        return Boolean.TRUE.equals(isSlo);
    }

    /**
     * 获取拼接后的 Server 端单点登录授权地址。
     *
     * @return 完整的授权地址
     */
    public String splicingAuthUrl() {
        return SaFoxUtil.spliceTwoUrl(getServerUrl(), getAuthUrl());
    }

    /**
     * 获取拼接后的 Server 端 Ticket 校验地址。
     *
     * @return 完整的 Ticket 校验地址
     */
    public String splicingCheckTicketUrl() {
        return SaFoxUtil.spliceTwoUrl(getServerUrl(), getCheckTicketUrl());
    }

    /**
     * 获取拼接后的 Server 端 userinfo 查询地址。
     *
     * @return 完整的 userinfo 查询地址
     */
    public String splicingUserinfoUrl() {
        return SaFoxUtil.spliceTwoUrl(getServerUrl(), getUserinfoUrl());
    }

    /**
     * 获取拼接后的 Server 端单点注销地址。
     *
     * @return 完整的单点注销地址
     */
    public String splicingSloUrl() {
        return SaFoxUtil.spliceTwoUrl(getServerUrl(), getSloUrl());
    }

}
