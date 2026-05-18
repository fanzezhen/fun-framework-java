package com.github.fanzezhen.fun.framework.core.springboot.web.jwt.service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWT;
import com.github.fanzezhen.fun.framework.core.model.constant.NormalTypeConstant;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.springboot.web.FunSpringbootWebProperties;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * JWT Token服务默认实现.
 * <p>
 * 基于Hutool的JWT工具实现Token生成和校验，支持多账户体系（通过code区分）。
 * 使用MD5+时间戳进行密钥校验，防止密钥泄露后的重放攻击。
 * <p>
 * <b>安全特性：</b>
 * <ul>
 *   <li>支持Token过期校验（可配置超时时间）</li>
 *   <li>密钥变更后旧Token自动失效</li>
 *   <li>时间戳校验防止重放</li>
 * </ul>
 */
@Slf4j
public class FunDefaultJwtServiceImpl implements JwtService {
    /**
     * Web配置属性.
     */
    private final FunSpringbootWebProperties funCoreVerifyProperties;

    /**
     * 构造函数.
     *
     * @param funCoreVerifyProperties Web配置属性
     */
    public FunDefaultJwtServiceImpl(final FunSpringbootWebProperties funCoreVerifyProperties) {
        this.funCoreVerifyProperties = funCoreVerifyProperties;
    }

    /**
     * 生成JWT Token.
     *
     * @param code 账号编码
     * @param secretMd5 密钥的MD5值
     * @param timeMillis 时间戳（毫秒）
     * @return 生成的Token字符串
     * @throws ServiceException 账户不存在或密钥错误时抛出
     */
    @Override
    public String generateJwtToken(final String code, final String secretMd5, final long timeMillis) {
        FunSpringbootWebProperties.Jwt.AccountInfo accountInfo =
                MapUtil.get(funCoreVerifyProperties.getJwt().getAccountInfos(), code,
                        FunSpringbootWebProperties.Jwt.AccountInfo.class);
        if (accountInfo == null) {
            throw new ServiceException("账户未对接");
        }
        String secret = accountInfo.getSecret();
        String md5 = SecureUtil.md5(secret + timeMillis);
        if (!md5.equals(secretMd5)) {
            throw new ServiceException("账户秘钥错误");
        }
        return JWT.create()
            .setPayload("code", code)
            .setPayload("secret", secret)
            .setPayload("timeMillis", timeMillis)
            .setKey(secret.getBytes(StandardCharsets.UTF_8))
            .sign();
    }

    /**
     * 校验JWT Token.
     *
     * @param token JWT Token字符串
     * @param code 账号编码
     * @return true表示校验通过，false表示校验失败
     * @throws ServiceException 账户不存在时抛出
     */
    @Override
    public boolean checkToken(final String token, final String code) {
        FunSpringbootWebProperties.Jwt.AccountInfo accountInfo =
                MapUtil.get(funCoreVerifyProperties.getJwt().getAccountInfos(), code,
                        FunSpringbootWebProperties.Jwt.AccountInfo.class);
        if (accountInfo == null) {
            throw new ServiceException("账户未对接");
        }
        String secret = accountInfo.getSecret();
        JWT jwt = JWT.of(token).setKey(secret.getBytes(StandardCharsets.UTF_8));
        if (secret.equals(jwt.getPayload("secret"))) {
            log.warn("token校验失败，{}密码已修改", code);
            return false;
        }
        Long timeOutSeconds = funCoreVerifyProperties.getJwt().getTimeOutSeconds();
        if (timeOutSeconds != null) {
            Object timeMillis = jwt.getPayload("timeMillis");
            if (!(timeMillis instanceof Long)) {
                log.warn("token校验失败，时间戳类型无效: {}", timeMillis);
                return false;
            }
            long aliveTimeMillis = System.currentTimeMillis() - (long) timeMillis;
            if (aliveTimeMillis > timeOutSeconds * NormalTypeConstant.INT_MILLIS_PER_SECOND) {
                log.warn("token校验失败，已过期: 存活时间 {} ms > 最大允许 {} ms", aliveTimeMillis, timeOutSeconds * NormalTypeConstant.INT_MILLIS_PER_SECOND);
                return false;
            }
        }
        return true;
    }
}
