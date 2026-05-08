package com.github.fanzezhen.fun.framework.core.verify.jwt.service;

/**
 */
public interface JwtService {

    /**
     * 生成 Token
     */
    String generateJwtToken(String code, String secretMd5, long timeMillis);
    /**
     * 校验 Token
     */
    boolean checkToken(String token, String timestamp);
    
}
