package com.github.fanzezhen.fun.framework.security.sa.token.sso;

import cn.dev33.satoken.context.model.SaRequest;

/**
 * Sa-Token-SSO 单点登录模块 工具类
 */
public final class SaSsoUtil {

    /**
     * 底层 SaSsoTemplate 对象
     */
    private static SaSsoTemplate ssoTemplate = new SaSsoTemplate();

    /**
     * 私有化构造函数
     */
    private SaSsoUtil() {
    }

    /**
     * 设置 SaSsoTemplate 对象
     */
    public static void setSsoTemplate(SaSsoTemplate ssoTemplate) {
        SaSsoUtil.ssoTemplate = ssoTemplate;
    }

    /**
     * 获取 SaSsoTemplate 对象
     */
    public static SaSsoTemplate getSsoTemplate() {
        return ssoTemplate;
    }

    // ---------------------- Ticket 操作 ----------------------

    /**
     * 删除 Ticket
     *
     * @param ticket Ticket码
     */
    public static void deleteTicket(String ticket) {
        ssoTemplate.deleteTicket(ticket);
    }

    /**
     * 删除 Ticket索引
     *
     * @param loginId 账号id
     */
    public static void deleteTicketIndex(Object loginId) {
        ssoTemplate.deleteTicketIndex(loginId);
    }

    /**
     * 根据 Ticket码 获取账号id，如果Ticket码无效则返回null
     *
     * @param ticket Ticket码
     *
     * @return 账号id
     */
    public static Object getLoginId(String ticket) {
        return ssoTemplate.getLoginId(ticket);
    }

    /**
     * 根据 Ticket码 获取账号id，并转换为指定类型
     *
     * @param <T>    要转换的类型
     * @param ticket Ticket码
     * @param cs     要转换的类型
     *
     * @return 账号id
     */
    public static <T> T getLoginId(String ticket, Class<T> cs) {
        return ssoTemplate.getLoginId(ticket, cs);
    }

    /**
     * 校验 Ticket 码，获取账号id，如果此ticket是有效的，则立即删除
     *
     * @param ticket Ticket码
     *
     * @return 账号id
     */
    public static Object checkTicket(String ticket) {
        return ssoTemplate.checkTicket(ticket);
    }

    /**
     * 校验ticket码，获取账号id，如果此ticket是有效的，则立即删除
     *
     * @param ticket Ticket码
     * @param client client 标识
     *
     * @return 账号id
     */
    public static Object checkTicket(String ticket, String client) {
        return ssoTemplate.checkTicket(ticket, client);
    }

    //
    // ------------------- SSO 模式三 -------------------

    /**
     * 构建URL：校验ticket的URL
     *
     * @param ticket           ticket码
     * @param ssoLogoutCallUrl 单点注销时的回调URL
     *
     * @return 构建完毕的URL
     */
    public static String buildCheckTicketUrl(String ticket, String ssoLogoutCallUrl) {
        return ssoTemplate.buildCheckTicketUrl(ticket, ssoLogoutCallUrl);
    }

    /**
     * 为指定账号id注册单点注销回调URL
     *
     * @param loginId        账号id
     * @param sloCallbackUrl 单点注销时的回调URL
     */
    public static void registerSloCallbackUrl(Object loginId, String sloCallbackUrl) {
        ssoTemplate.registerSloCallbackUrl(loginId, sloCallbackUrl);
    }

    /**
     * 构建URL：单点注销URL
     *
     * @param loginId 要注销的账号id
     *
     * @return 单点注销URL
     */
    public static String buildSloUrl(Object loginId) {
        return ssoTemplate.buildSloUrl(loginId);
    }

    /**
     * 构建URL：Server端 单点登录地址
     *
     * @param back 回调路径
     *
     * @return [SSO-Server端-认证地址 ]
     */
    public static String buildServerAuthUrl(String redirect, String back) {
        return ssoTemplate.buildServerAuthUrl(redirect, back);
    }

    /**
     * 构建URL：Server端 账号资料查询地址
     *
     * @param loginId 账号id
     *
     * @return Server端 账号资料查询地址
     */
    public static String buildUserinfoUrl(Object loginId) {
        return ssoTemplate.buildUserinfoUrl(loginId);
    }


    // ------------------- 请求相关 -------------------

    /**
     * 根据参数计算签名
     *
     * @param loginId   账号id
     * @param timestamp 当前时间戳，13位
     * @param nonce     随机字符串
     *
     * @return 签名
     */
    public static String getSign(Object loginId, String timestamp, String nonce) {
        return ssoTemplate.getSign(loginId, timestamp, nonce);
    }

    /**
     * 给 url 追加 sign 等参数
     *
     * @param url     连接
     * @param loginId 账号id
     *
     * @return 加工后的url
     */
    public static String addSignParams(String url, Object loginId) {
        return ssoTemplate.addSignParams(url, loginId);
    }

    /**
     * 校验签名
     *
     * @param req request
     */
    public static void checkSign(SaRequest req) {
        ssoTemplate.checkSign(req);
    }

    /**
     * 校验时间戳与当前时间的差距是否超出限制
     *
     * @param timestamp 时间戳
     */
    public static void checkTimestamp(long timestamp) {
        ssoTemplate.checkTimestamp(timestamp);
    }

}
