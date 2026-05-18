package com.github.fanzezhen.fun.framework.security.sa.token.sso;

import cn.dev33.satoken.context.model.SaRequest;

/**
 * Sa-Token SSO 单点登录模块工具类。
 * <p>
 * 提供 SSO 模板对象的访问和各种便捷方法。
 */
@SuppressWarnings("lombok")
public final class SaSsoUtil {

    /**
     * SSO 模板对象。
     */
    private static SaSsoTemplate ssoTemplate = new SaSsoTemplate();

    /**
     * 私有构造方法，禁止实例化。
     */
    private SaSsoUtil() {
    }

    /**
     * 设置 SSO 模板对象。
     *
     * @param ssoTemplate SSO 模板对象
     */
    public static void setSsoTemplate(final SaSsoTemplate ssoTemplate) {
        SaSsoUtil.ssoTemplate = ssoTemplate;
    }

    /**
     * 获取 SSO 模板对象。
     *
     * @return SSO 模板对象
     */
    public static SaSsoTemplate getSsoTemplate() {
        return ssoTemplate;
    }

    // ---------------------- Ticket 操作 ----------------------

    /**
     * 删除 Ticket。
     *
     * @param ticket Ticket码
     */
    public static void deleteTicket(final String ticket) {
        ssoTemplate.deleteTicket(ticket);
    }

    /**
     * 删除 Ticket 索引。
     *
     * @param loginId 账号ID
     */
    public static void deleteTicketIndex(final Object loginId) {
        ssoTemplate.deleteTicketIndex(loginId);
    }

    /**
     * 根据 Ticket 码获取账号ID。
     *
     * @param ticket Ticket码
     * @return 账号ID，如果 Ticket 码无效则返回 null
     */
    public static Object getLoginId(final String ticket) {
        return ssoTemplate.getLoginId(ticket);
    }

    /**
     * 根据 Ticket 码获取账号ID，并转换为指定类型。
     *
     * @param <T>    要转换的类型
     * @param ticket Ticket码
     * @param cs     要转换的类型
     * @return 账号ID
     */
    public static <T> T getLoginId(final String ticket, final Class<T> cs) {
        return ssoTemplate.getLoginId(ticket, cs);
    }

    /**
     * 校验 Ticket 码并获取账号ID。
     * <p>
     * 如果 Ticket 有效，则立即删除该 Ticket（一次性使用）。
     *
     * @param ticket Ticket码
     * @return 账号ID
     */
    public static Object checkTicket(final String ticket) {
        return ssoTemplate.checkTicket(ticket);
    }

    /**
     * 校验 Ticket 码并获取账号ID。
     * <p>
     * 如果 Ticket 有效，则立即删除该 Ticket（一次性使用）。
     *
     * @param ticket Ticket码
     * @param client Client 标识
     * @return 账号ID
     */
    public static Object checkTicket(final String ticket, final String client) {
        return ssoTemplate.checkTicket(ticket, client);
    }

    // ------------------- SSO 模式三 -------------------

    /**
     * 构建校验 Ticket 的URL。
     *
     * @param ticket           Ticket码
     * @param ssoLogoutCallUrl 单点注销时的回调URL
     * @return 校验 Ticket 的完整URL
     */
    public static String buildCheckTicketUrl(final String ticket, final String ssoLogoutCallUrl) {
        return ssoTemplate.buildCheckTicketUrl(ticket, ssoLogoutCallUrl);
    }

    /**
     * 为指定账号注册单点注销回调URL。
     *
     * @param loginId        账号ID
     * @param sloCallbackUrl 单点注销时的回调URL
     */
    public static void registerSloCallbackUrl(final Object loginId, final String sloCallbackUrl) {
        ssoTemplate.registerSloCallbackUrl(loginId, sloCallbackUrl);
    }

    /**
     * 构建单点注销URL。
     *
     * @param loginId 要注销的账号ID
     * @return 单点注销URL
     */
    public static String buildSloUrl(final Object loginId) {
        return ssoTemplate.buildSloUrl(loginId);
    }

    /**
     * 构建 Server 端单点登录认证地址。
     *
     * @param redirect 重定向地址
     * @param back     回调路径
     * @return SSO Server端认证地址
     */
    public static String buildServerAuthUrl(final String redirect, final String back) {
        return ssoTemplate.buildServerAuthUrl(redirect, back);
    }

    /**
     * 构建 Server 端账号资料查询地址。
     *
     * @param loginId 账号ID
     * @return 账号资料查询URL
     */
    public static String buildUserinfoUrl(final Object loginId) {
        return ssoTemplate.buildUserinfoUrl(loginId);
    }


    // ------------------- 请求相关 -------------------

    /**
     * 根据参数计算签名。
     *
     * @param loginId   账号ID
     * @param timestamp 当前时间戳（13位）
     * @param nonce     随机字符串
     * @return 签名字符串
     */
    public static String getSign(final Object loginId, final String timestamp, final String nonce) {
        return ssoTemplate.getSign(loginId, timestamp, nonce);
    }

    /**
     * 给 URL 追加签名等参数。
     *
     * @param url     原始URL
     * @param loginId 账号ID
     * @return 添加签名参数后的URL
     */
    public static String addSignParams(final String url, final Object loginId) {
        return ssoTemplate.addSignParams(url, loginId);
    }

    /**
     * 校验签名。
     * <p>
     * 如果签名无效，会抛出运行时异常。
     *
     * @param req 请求对象
     */
    public static void checkSign(final SaRequest req) {
        ssoTemplate.checkSign(req);
    }

    /**
     * 校验时间戳与当前时间的差距是否超出限制。
     * <p>
     * 如果时间戳超出允许范围，会抛出运行时异常。
     *
     * @param timestamp 时间戳（毫秒）
     */
    public static void checkTimestamp(final long timestamp) {
        ssoTemplate.checkTimestamp(timestamp);
    }

}
