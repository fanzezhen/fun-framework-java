package com.github.fanzezhen.fun.framework.security.sa.token.oauth;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.security.sa.token.enums.SecurityExceptionEnum;
import com.github.fanzezhen.fun.framework.security.sa.token.oauth.config.SaOauthConfig;
import com.github.fanzezhen.fun.framework.security.sa.token.oauth.name.ApiName;
import com.github.fanzezhen.fun.framework.security.sa.token.oauth.name.ParamName;
import lombok.Data;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Sa-Token OAuth2.0 单点登录模板类。
 * <p>
 * 提供 OAuth2.0 单点登录的核心功能，包括票据管理、URL构建、单点注销等。
 */
@Data
public class SaOauthTemplate {

    // ---------------------- 全局配置 ----------------------

    /**
     * URL 连接符（?= 或 &=）的长度。
     */
    private static final int CONTACT_URL_LENGTH = 2;

    /**
     * 所有 API 名称配置。
     */
    private ApiName apiName = new ApiName();

    /**
     * 所有参数名称配置。
     */
    private ParamName paramName = new ParamName();

    /**
     * 设置参数名称配置对象。
     *
     * @param paramName 参数名称对象
     * @return 对象自身（支持链式调用）
     */
    public SaOauthTemplate setParamName(final ParamName paramName) {
        this.paramName = paramName;
        return this;
    }

    /**
     * 设置 API 名称配置对象。
     *
     * @param apiName API 名称对象
     * @return 对象自身（支持链式调用）
     */
    public SaOauthTemplate setApiName(final ApiName apiName) {
        this.apiName = apiName;
        return this;
    }

    /**
     * 获取底层使用的会话逻辑对象。
     *
     * @return StpLogic 对象
     */
    public StpLogic getStpLogic() {
        return StpUtil.stpLogic;
    }

    /**
     * 获取底层使用的 OAuth2.0 配置对象。
     *
     * @return OAuth2.0 配置对象
     */
    public SaOauthConfig getSsoConfig() {
        return SaOauthManager.getConfig();
    }

    /**
     * 根据 Ticket 码获取账号ID。
     *
     * @param ticket Ticket码
     * @return 账号ID，如果 Ticket 码无效则返回 null
     */
    public Object getLoginId(final String ticket) {
        if (SaFoxUtil.isEmpty(ticket)) {
            return null;
        }
        String loginId = SaManager.getSaTokenDao().get(splicingTicketSaveKey(ticket));
        // 如果是 "a,b" 的格式，则只取最前面的一项
        if (loginId != null && loginId.indexOf(",") > -1) {
            String[] arr = loginId.split(",");
            loginId = arr[0];
        }
        return loginId;
    }

    /**
     * 根据 Ticket 码获取账号ID，并转换为指定类型。
     *
     * @param <T>    要转换的类型
     * @param ticket Ticket码
     * @param cs     要转换的类型
     * @return 账号ID
     */
    public <T> T getLoginId(final String ticket, final Class<T> cs) {
        return SaFoxUtil.getValueByType(getLoginId(ticket), cs);
    }

    /**
     * 删除 Ticket。
     *
     * @param ticket Ticket码
     */
    public void deleteTicket(final String ticket) {
        if (ticket == null) {
            return;
        }
        SaManager.getSaTokenDao().delete(splicingTicketSaveKey(ticket));
    }

    // ------------------- OAuth 模式三相关 -------------------

    /**
     * 删除 Ticket 索引。
     *
     * @param loginId 账号ID
     */
    public void deleteTicketIndex(final Object loginId) {
        if (loginId == null) {
            return;
        }
        SaManager.getSaTokenDao().delete(splicingTicketIndexKey(loginId));
    }

    /**
     * 为指定账号注册单点注销回调URL。
     *
     * @param loginId        账号ID
     * @param sloCallbackUrl 单点注销时的回调URL
     */
    public void registerSloCallbackUrl(final Object loginId, final String sloCallbackUrl) {
        if (SaFoxUtil.isEmpty(loginId) || SaFoxUtil.isEmpty(sloCallbackUrl)) {
            return;
        }
        SaSession session = getStpLogic().getSessionByLoginId(loginId);
        Set<String> urlSet = session.get(SaOauthConstant.SLO_CALLBACK_SET_KEY, HashSet::new);
        urlSet.add(sloCallbackUrl);
        session.set(SaOauthConstant.SLO_CALLBACK_SET_KEY, urlSet);
    }

    // ---------------------- 构建URL ----------------------

    /**
     * 指定账号单点注销。
     *
     * @param loginId 指定账号ID
     */
    public void ssoLogout(final Object loginId) {

        // 如果这个账号尚未登录，则无操作
        SaSession session = getStpLogic().getSessionByLoginId(loginId, false);
        if (session == null) {
            return;
        }

        // step.1 遍历通知 Client 端注销会话
        SaOauthConfig cfg = SaOauthManager.getConfig();
        Set<String> urlSet = session.get(SaOauthConstant.SLO_CALLBACK_SET_KEY, HashSet::new);
        for (String url : urlSet) {
            cfg.getSendHttp().apply(url);
        }

        // step.2 Server端注销
        getStpLogic().logout(loginId);
    }

    /**
     * 构建 Server 端单点登录认证地址。
     *
     * @param back 回调路径
     * @return OAuth2.0 Server端认证地址
     */
    public String buildServerAuthUrl(final String back) {

        SaOauthConfig config = SaOauthManager.getConfig();
        // 服务端认证地址
        String serverUrl = config.getAuthorizeUrl();

        // 拼接客户端标识
        String clientId = config.getClientId();
        if (SaFoxUtil.isNotEmpty(clientId)) {
            serverUrl = SaFoxUtil.joinParam(serverUrl, paramName.getClientId(), clientId);
        }
        // response_type=code
        serverUrl = SaFoxUtil.joinParam(serverUrl, paramName.getResponseType(), config.getResponseType());


        serverUrl = SaFoxUtil.joinParam(serverUrl, paramName.getScope(), config.getScope());

        String encodedBack = SaFoxUtil.encodeUrl(back);

        return SaFoxUtil.joinParam(serverUrl, paramName.getRedirectUri(), encodedBack);
    }


    /**
     * 对 URL 中的 back 参数进行编码。
     * <p>
     * 解决超链接重定向后参数丢失的问题。
     *
     * @param url 原始URL
     * @return 编码后的URL
     */
    public String encodeBackParam(final String url) {

        // 获取back参数所在位置
        int index = url.indexOf("?" + paramName.getRedirectUri() + "=");
        if (index == -1) {
            index = url.indexOf("&" + paramName.getRedirectUri() + "=");
            if (index == -1) {
                return url;
            }
        }

        // 开始编码
        int length = paramName.getRedirectUri().length() + CONTACT_URL_LENGTH;
        String back = url.substring(index + length);
        String encodedBack = SaFoxUtil.encodeUrl(back);

        // 放回url中
        return url.substring(0, index + length) + encodedBack;
    }

    // ------------------- 返回相应key -------------------

    /**
     * 拼接 Ticket 存储的 Key。
     * <p>
     * 用于通过 Ticket 查询账号ID。
     *
     * @param ticket Ticket值
     * @return 存储Key
     */
    public String splicingTicketSaveKey(final String ticket) {
        return SaManager.getConfig().getTokenName() + ":ticket:" + ticket;
    }

    /**
     * 拼接 Ticket 索引的 Key。
     * <p>
     * 用于通过账号ID反查 Ticket。
     *
     * @param id 账号ID
     * @return 索引Key
     */
    public String splicingTicketIndexKey(final Object id) {
        return SaManager.getConfig().getTokenName() + ":id-ticket:" + id;
    }


    // ------------------- 请求相关 -------------------

    /**
     * 发送HTTP请求并返回结果。
     *
     * @param url 请求地址
     * @return 请求结果
     */
    public SaResult request(final String url) {
        String body = SaOauthManager.getConfig().getSendHttp().apply(url);
        Map<String, Object> map = SaManager.getSaJsonTemplate().jsonToMap(body);
        return new SaResult(map);
    }

    /**
     * 校验时间戳与当前时间的差距是否超出限制。
     *
     * @param timestamp 时间戳（毫秒）
     * @throws ServiceException 如果时间戳超出允许范围
     */
    public void checkTimestamp(final long timestamp) {
        long disparity = Math.abs(System.currentTimeMillis() - timestamp);
        long allowDisparity = SaOauthManager.getConfig().getTimestampDisparity();
        if (allowDisparity != -1 && disparity > allowDisparity) {
            throw new ServiceException(SecurityExceptionEnum.TIMESTAMP_OUT_RANGE, allowDisparity);
        }
    }


    /**
     * 构建 Client 端单点注销地址。
     *
     * @param cfg         OAuth2.0 配置对象
     * @param redirectUrl 注销后的重定向URL
     * @return 单点注销URL
     */
    public String buildLogoutUrl(final SaOauthConfig cfg, final String redirectUrl) {
        String url = SaOauthManager.getConfig().getLogoutUrl();
        // 追加到url
        url = SaFoxUtil.joinParam(url, paramName.getResponseType(), cfg.getResponseType());
        url = SaFoxUtil.joinParam(url, paramName.getClientId(), cfg.getClientId());
        url = SaFoxUtil.joinParam(url, paramName.getRedirectUri(), redirectUrl);
        return url;
    }
}
