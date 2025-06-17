package com.github.fanzezhen.fun.framework.core.verify.jwt.service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWT;
import com.github.fanzezhen.fun.framework.core.exception.ExceptionUtil;
import com.github.fanzezhen.fun.framework.core.verify.FunCoreVerifyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * @author fanzezhen
 */
@Slf4j
@Service
@ConditionalOnMissingBean(value = JwtService.class, ignored = FunDefaultJwtService.class)
public class FunDefaultJwtService implements JwtService {
    @Resource
    private FunCoreVerifyProperties funCoreVerifyProperties;

    /**
     * 生成 Token
     */
    @Override
    public String generateJwtToken(String code, String secretMd5, long timeMillis) {
        FunCoreVerifyProperties.Jwt.AccountInfo accountInfo = MapUtil.get(funCoreVerifyProperties.getJwt().getAccountInfos(), code, FunCoreVerifyProperties.Jwt.AccountInfo.class);
        if (accountInfo == null) {
            throw ExceptionUtil.wrapException("账户未对接");
        }
        String secret = accountInfo.getSecret();
        String md5 = SecureUtil.md5(secret + timeMillis);
        if (!md5.equals(secretMd5)) {
            throw ExceptionUtil.wrapException("账户秘钥错误");
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
        FunCoreVerifyProperties.Jwt.AccountInfo accountInfo = MapUtil.get(funCoreVerifyProperties.getJwt().getAccountInfos(), code, FunCoreVerifyProperties.Jwt.AccountInfo.class);
        if (accountInfo == null) {
            throw ExceptionUtil.wrapException("账户未对接");
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
