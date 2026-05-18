package com.github.fanzezhen.fun.framework.core.springboot.web.jwt.service;

/**
 * JWT Token服务接口.
 * <p>
 * 提供JWT Token的生成和校验功能。
 */
public interface JwtService {

    /**
     * 生成JWT Token.
     *
     * @param code 账号编码
     * @param secretMd5 密钥的MD5值
     * @param timeMillis 时间戳（毫秒）
     * @return 生成的Token字符串
     */
    String generateJwtToken(String code, String secretMd5, long timeMillis);

    /**
     * 校验JWT Token.
     *
     * @param token JWT Token字符串
     * @param timestamp 时间戳字符串
     * @return true表示校验通过，false表示校验失败
     */
    boolean checkToken(String token, String timestamp);

}
