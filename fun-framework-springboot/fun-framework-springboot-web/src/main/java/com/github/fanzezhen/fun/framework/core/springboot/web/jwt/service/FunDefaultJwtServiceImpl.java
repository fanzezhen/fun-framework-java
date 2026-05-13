package com.github.fanzezhen.fun.framework.core.springboot.web.jwt.service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWT;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.springboot.web.FunSpringbootWebProperties;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * JWT Token服务默认实现
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
 *
 */
@Slf4j
public class FunDefaultJwtServiceImpl implements JwtService {
    private final FunSpringbootWebProperties funCoreVerifyProperties;

    public FunDefaultJwtServiceImpl(FunSpringbootWebProperties funCoreVerifyProperties) {
        this.funCoreVerifyProperties = funCoreVerifyProperties;
    }

    /**
     * 生成 Token
     */
    @Override
    public String generateJwtToken(String code, String secretMd5, long timeMillis) {
        FunSpringbootWebProperties.Jwt.AccountInfo accountInfo = MapUtil.get(funCoreVerifyProperties.getJwt().getAccountInfos(), code, FunSpringbootWebProperties.Jwt.AccountInfo.class);
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

    @Override
    public boolean checkToken(String token, String code) {
        FunSpringbootWebProperties.Jwt.AccountInfo accountInfo = MapUtil.get(funCoreVerifyProperties.getJwt().getAccountInfos(), code, FunSpringbootWebProperties.Jwt.AccountInfo.class);
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
                log.warn("token校验失败，已过期 {} > {} ", timeMillis, timeOutSeconds * 1000);
                return false;
            }
            long aliveTimeMillis = System.currentTimeMillis() - (long) timeMillis;
            if (aliveTimeMillis > timeOutSeconds * 1000) {
                log.warn("token校验失败，已过期 {} > {}", aliveTimeMillis, timeOutSeconds * 1000);
                return false;
            }
        }
        return true;
    }
}
