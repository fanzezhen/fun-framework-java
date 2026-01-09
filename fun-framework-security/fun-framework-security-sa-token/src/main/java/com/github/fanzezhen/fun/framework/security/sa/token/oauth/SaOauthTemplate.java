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
 * Sa-Token-SSO 单点登录模块
 */
@Data
public class SaOauthTemplate {

    // ---------------------- 全局配置 ----------------------

    /**
     * ?= 或者&= 连接符的长度
     */
    private static final int CONTACT_URL_LENGTH = 2;
    /**
     * 所有 API 名称
     */
    private ApiName apiName = new ApiName();
    /**
     * 所有参数名称
     */
    private ParamName paramName = new ParamName();

    /**
     * @param paramName 替换 paramName 对象
     *
     * @return 对象自身
     */
    public SaOauthTemplate setParamName(ParamName paramName) {
        this.paramName = paramName;
        return this;
    }

    /**
     * @param apiName 替换 apiName 对象
     *
     * @return 对象自身
     */
    public SaOauthTemplate setApiName(ApiName apiName) {
        this.apiName = apiName;
        return this;
    }

    /**
     * 获取底层使用的会话对象
     *
     * @return /
     */
    public StpLogic getStpLogic() {
        return StpUtil.stpLogic;
    }

    /**
     * 获取底层使用的配置对象
     *
     * @return /
     */
    public SaOauthConfig getSsoConfig() {
        return SaOauthManager.getConfig();
    }

    /**
     * 根据 Ticket码 获取账号id，如果Ticket码无效则返回null
     *
     * @param ticket Ticket码
     *
     * @return 账号id
     */
    public Object getLoginId(String ticket) {
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
     * 根据 Ticket码 获取账号id，并转换为指定类型
     *
     * @param <T>    要转换的类型
     * @param ticket Ticket码
     * @param cs     要转换的类型
     *
     * @return 账号id
     */
    public <T> T getLoginId(String ticket, Class<T> cs) {
        return SaFoxUtil.getValueByType(getLoginId(ticket), cs);
    }

    /**
     * 删除 Ticket
     *
     * @param ticket Ticket码
     */
    public void deleteTicket(String ticket) {
        if (ticket == null) {
            return;
        }
        SaManager.getSaTokenDao().delete(splicingTicketSaveKey(ticket));
    }

    // ------------------- SSO 模式三相关 -------------------

    /**
     * 删除 Ticket索引
     *
     * @param loginId 账号id
     */
    public void deleteTicketIndex(Object loginId) {
        if (loginId == null) {
            return;
        }
        SaManager.getSaTokenDao().delete(splicingTicketIndexKey(loginId));
    }

    /**
     * 为指定账号id注册单点注销回调URL
     *
     * @param loginId        账号id
     * @param sloCallbackUrl 单点注销时的回调URL
     */
    public void registerSloCallbackUrl(Object loginId, String sloCallbackUrl) {
        if (SaFoxUtil.isEmpty(loginId) || SaFoxUtil.isEmpty(sloCallbackUrl)) {
            return;
        }
        SaSession session = getStpLogic().getSessionByLoginId(loginId);
        Set<String> urlSet = session.get(SaOauthConstant.SLO_CALLBACK_SET_KEY, () -> new HashSet<String>());
        urlSet.add(sloCallbackUrl);
        session.set(SaOauthConstant.SLO_CALLBACK_SET_KEY, urlSet);
    }

    // ---------------------- 构建URL ----------------------

    /**
     * 指定账号单点注销
     *
     * @param loginId 指定账号
     */
    public void ssoLogout(Object loginId) {

        // 如果这个账号尚未登录，则无操作
        SaSession session = getStpLogic().getSessionByLoginId(loginId, false);
        if (session == null) {
            return;
        }

        // step.1 遍历通知 Client 端注销会话
        SaOauthConfig cfg = SaOauthManager.getConfig();
        Set<String> urlSet = session.get(SaOauthConstant.SLO_CALLBACK_SET_KEY, () -> new HashSet<String>());
        for (String url : urlSet) {
            cfg.getSendHttp().apply(url);
        }

        // step.2 Server端注销
        getStpLogic().logout(loginId);
    }

    /**
     * 构建URL：Server端 单点登录地址
     *
     * @param back 回调路径
     *
     * @return [SSO-Server端-认证地址 ]
     */
    public String buildServerAuthUrl(String back) {

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

        back = SaFoxUtil.encodeUrl(back);

        return SaFoxUtil.joinParam(serverUrl, paramName.getRedirectUri(), back);
    }


    /**
     * 对url中的back参数进行URL编码, 解决超链接重定向后参数丢失的bug
     *
     * @param url url
     *
     * @return 编码过后的url
     */
    public String encodeBackParam(String url) {

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
        back = SaFoxUtil.encodeUrl(back);

        // 放回url中
        url = url.substring(0, index + length) + back;
        return url;
    }

    // ------------------- 返回相应key -------------------

    /**
     * 拼接key：Ticket 查 账号Id
     *
     * @param ticket ticket值
     *
     * @return key
     */
    public String splicingTicketSaveKey(String ticket) {
        return SaManager.getConfig().getTokenName() + ":ticket:" + ticket;
    }

    /**
     * 拼接key：账号Id 反查 Ticket
     *
     * @param id 账号id
     *
     * @return key
     */
    public String splicingTicketIndexKey(Object id) {
        return SaManager.getConfig().getTokenName() + ":id-ticket:" + id;
    }


    // ------------------- 请求相关 -------------------

    /**
     * 发出请求，并返回 SaResult 结果
     *
     * @param url 请求地址
     *
     * @return 返回的结果
     */
    public SaResult request(String url) {
        String body = SaOauthManager.getConfig().getSendHttp().apply(url);
        Map<String, Object> map = SaManager.getSaJsonTemplate().jsonToMap(body);
        return new SaResult(map);
    }

    /**
     * 校验时间戳与当前时间的差距是否超出限制
     *
     * @param timestamp 时间戳
     */
    public void checkTimestamp(long timestamp) {
        long disparity = Math.abs(System.currentTimeMillis() - timestamp);
        long allowDisparity = SaOauthManager.getConfig().getTimestampDisparity();
        if (allowDisparity != -1 && disparity > allowDisparity) {
            throw new ServiceException(SecurityExceptionEnum.TIMESTAMP_OUT_RANGE, allowDisparity);
        }
    }


    /**
     * 构建URL：Client端 单点注销地址
     */
    public String buildLogoutUrl(SaOauthConfig cfg, String redirectUrl) {
        String url = SaOauthManager.getConfig().getLogoutUrl();
        // 追加到url
        url = SaFoxUtil.joinParam(url, paramName.getResponseType(), cfg.getResponseType());
        url = SaFoxUtil.joinParam(url, paramName.getClientId(), cfg.getClientId());
        url = SaFoxUtil.joinParam(url, paramName.getRedirectUri(), redirectUrl);
        return url;
    }
}
