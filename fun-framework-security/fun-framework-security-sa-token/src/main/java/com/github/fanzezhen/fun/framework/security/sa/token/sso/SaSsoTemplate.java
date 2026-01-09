package com.github.fanzezhen.fun.framework.security.sa.token.sso;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.security.sa.token.enums.SecurityExceptionEnum;
import com.github.fanzezhen.fun.framework.security.sa.token.sso.config.SaSsoConfig;
import com.github.fanzezhen.fun.framework.security.sa.token.sso.name.ApiName;
import com.github.fanzezhen.fun.framework.security.sa.token.sso.name.ParamName;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Sa-Token-SSO 单点登录模块
 */
public class SaSsoTemplate {

    // ---------------------- 全局配置 ----------------------

    /**
     * 获取随机字符串
     */

    private static final int RANDOM_STRING_LENGTH = 20;
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
    public SaSsoTemplate setParamName(ParamName paramName) {
        this.paramName = paramName;
        return this;
    }

    /**
     * @param apiName 替换 apiName 对象
     *
     * @return 对象自身
     */
    public SaSsoTemplate setApiName(ApiName apiName) {
        this.apiName = apiName;
        return this;
    }

    /**
     * 获取所有参数名称
     *
     * @return /
     */
    public ParamName getParamName() {
        return paramName;
    }


    /**
     * 获取所有API名称
     *
     * @return /
     */
    public ApiName getApiName() {
        return apiName;
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
    public SaSsoConfig getSsoConfig() {
        return SaSsoManager.getConfig();
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
     * 校验 Ticket 码，获取账号id，如果此ticket是有效的，则立即删除
     *
     * @param ticket Ticket码
     *
     * @return 账号id
     */
    public Object checkTicket(String ticket) {
        return checkTicket(ticket, getSsoConfig().getClient());
    }

    /**
     * 校验 Ticket 码，获取账号id，如果此ticket是有效的，则立即删除
     *
     * @param ticket Ticket码
     * @param client client 标识
     *
     * @return 账号id
     */
    public Object checkTicket(String ticket, String client) {
        // 读取 loginId
        String loginId = SaManager.getSaTokenDao().get(splicingTicketSaveKey(ticket));

        if (loginId != null) {

            // 如果是 "a,b" 的格式，则解析出对应的 Client
            String ticketClient = null;
            if (loginId.indexOf(",") > -1) {
                String[] arr = loginId.split(",");
                loginId = arr[0];
                ticketClient = arr[1];
            }

            // 如果指定了 client 标识，则校验一下 client 标识是否一致
            if (SaFoxUtil.isNotEmpty(client) && SaFoxUtil.notEquals(client, ticketClient)) {
                throw new ServiceException(SecurityExceptionEnum.CONSISTENCY_VERIFICATION_TICKET_CLIENT, client, ticket);
            }
            // 删除 ticket 信息，使其只有一次性有效
            deleteTicket(ticket);
            deleteTicketIndex(loginId);
        }

        //
        return loginId;
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
        Set<String> urlSet = session.get(SaSsoConstant.SLO_CALLBACK_SET_KEY, () -> new HashSet<String>());
        urlSet.add(sloCallbackUrl);
        session.set(SaSsoConstant.SLO_CALLBACK_SET_KEY, urlSet);
    }

    /**
     * 构建URL：Server端 单点登录地址
     *
     * @param clientLoginUrl Client端登录地址
     * @param back           回调路径
     *
     * @return [SSO-Server端-认证地址 ]
     */
    public String buildServerAuthUrl(String clientLoginUrl, String back) {

        // 服务端认证地址
        String serverUrl = SaSsoManager.getConfig().splicingAuthUrl();

        // 拼接客户端标识
        String client = SaSsoManager.getConfig().getClient();
        if (SaFoxUtil.isNotEmpty(client)) {
            serverUrl = SaFoxUtil.joinParam(serverUrl, paramName.getClient(), client);
        }

        // 对back地址编码
        back = (back == null ? "" : back);
        back = SaFoxUtil.encodeUrl(back);

        // 开始拼接 sso 统一认证地址，形如：serverAuthUrl = http://xxx.com?redirectUrl=xxx.com?back=xxx.com

        /*
         * 部分 Servlet 版本 request.getRequestURL() 返回的 url 带有 query 参数，形如：http://domain.com?id=1，
         * 如果不加判断会造成最终生成的 serverAuthUrl 带有双 back 参数 ，这个 if 判断正是为了解决此问题
         */
        if (!clientLoginUrl.contains(paramName.getBack() + "=" + back)) {
            clientLoginUrl = SaFoxUtil.joinParam(clientLoginUrl, paramName.getBack(), back);
        }
        return SaFoxUtil.joinParam(serverUrl, paramName.getRedirect(), clientLoginUrl);
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
        int index = url.indexOf("?" + paramName.getBack() + "=");
        if (index == -1) {
            index = url.indexOf("&" + paramName.getBack() + "=");
            if (index == -1) {
                return url;
            }
        }

        // 开始编码
        int length = paramName.getBack().length() + "?=".length();
        String back = url.substring(index + length);
        back = SaFoxUtil.encodeUrl(back);

        // 放回url中
        url = url.substring(0, index + length) + back;
        return url;
    }

    /**
     * 构建URL：Server端 账号资料查询地址
     *
     * @param loginId 账号id
     *
     * @return Server端 账号资料查询地址
     */
    public String buildUserinfoUrl(Object loginId) {
        String userinfoUrl = SaSsoManager.getConfig().splicingUserinfoUrl();
        return addSignParams(userinfoUrl, loginId);
    }

    /**
     * 构建URL：校验ticket的URL
     * <p>
     * 在模式三下，Client端拿到Ticket后根据此地址向Server端发送请求，获取账号id
     *
     * @param ticket           ticket码
     * @param ssoLogoutCallUrl 单点注销时的回调URL
     *
     * @return 构建完毕的URL
     */
    public String buildCheckTicketUrl(String ticket, String ssoLogoutCallUrl) {
        // 裸地址
        String url = SaSsoManager.getConfig().splicingCheckTicketUrl();

        // 拼接 client 参数
        String client = getSsoConfig().getClient();
        if (SaFoxUtil.isNotEmpty(client)) {
            url = SaFoxUtil.joinParam(url, paramName.getClient(), client);
        }

        // 拼接ticket参数
        url = SaFoxUtil.joinParam(url, paramName.getTicket(), ticket);

        // 拼接单点注销时的回调URL
        if (ssoLogoutCallUrl != null) {
            url = SaFoxUtil.joinParam(url, paramName.getSsoLogoutCall(), ssoLogoutCallUrl);
        }

        // 返回
        return url;
    }


    // ------------------- 返回相应key -------------------

    /**
     * 构建URL：单点注销URL
     *
     * @param loginId 要注销的账号id
     *
     * @return 单点注销URL
     */
    public String buildSloUrl(Object loginId) {
        String url = SaSsoManager.getConfig().splicingSloUrl();
        return addSignParams(url, loginId);
    }

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


    // ------------------- 请求相关 -------------------

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


    /**
     * 获取：接口调用秘钥
     *
     * @return see note
     */
    public String getSecretKey() {
        // 默认从配置文件中返回
        return SaSsoManager.getConfig().getSecretKey();
    }

    /**
     * 根据参数计算签名
     *
     * @param loginId   账号id
     * @param timestamp 当前时间戳，13位
     * @param nonce     随机字符串
     *
     * @return 签名
     */
    public String getSign(Object loginId, String timestamp, String nonce) {
        Map<String, Object> map = new TreeMap<>();
        map.put(paramName.getLoginId(), loginId);
        map.put(paramName.getTimestamp(), timestamp);
        map.put(paramName.getNonce(), nonce);
        return SaSignManager.getSaSignTemplate().createSign(map);
    }

    /**
     * 给 url 追加 sign 等参数
     *
     * @param url     连接
     * @param loginId 账号id
     *
     * @return 加工后的url
     */
    public String addSignParams(String url, Object loginId) {

        // 时间戳、随机字符串、参数签名
        String timestamp = String.valueOf(System.currentTimeMillis());
        String nonce = SaFoxUtil.getRandomString(RANDOM_STRING_LENGTH);
        String sign = getSign(loginId, timestamp, nonce);

        // 追加到url
        url = SaFoxUtil.joinParam(url, paramName.getLoginId(), loginId);
        url = SaFoxUtil.joinParam(url, paramName.getTimestamp(), timestamp);
        url = SaFoxUtil.joinParam(url, paramName.getNonce(), nonce);
        url = SaFoxUtil.joinParam(url, paramName.getSign(), sign);
        return url;
    }

    /**
     * 校验签名
     *
     * @param req request
     */
    public void checkSign(SaRequest req) {

        // 参数签名、账号id、时间戳、随机字符串
        String sign = req.getParamNotNull(paramName.getSign());
        String loginId = req.getParamNotNull(paramName.getLoginId());
        String timestamp = req.getParamNotNull(paramName.getTimestamp());
        String nonce = req.getParamNotNull(paramName.getNonce());

        // 校验时间戳
        checkTimestamp(Long.valueOf(timestamp));

        // 校验签名
        String calcSign = getSign(loginId, timestamp, nonce);
        if (!calcSign.equals(sign)) {
            throw new ServiceException(SecurityExceptionEnum.SIGN_INVALID,  calcSign);
        }
    }

    /**
     * 校验时间戳与当前时间的差距是否超出限制
     *
     * @param timestamp 时间戳
     */
    public void checkTimestamp(long timestamp) {
        long disparity = Math.abs(System.currentTimeMillis() - timestamp);
        long allowDisparity = SaSsoManager.getConfig().getTimestampDisparity();
        if (allowDisparity != -1 && disparity > allowDisparity) {
            throw new ServiceException(SecurityExceptionEnum.TIMESTAMP_OUT_RANGE, allowDisparity);
        }
    }

}
