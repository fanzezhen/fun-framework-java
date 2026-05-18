package com.github.fanzezhen.fun.framework.core.springboot.web.jwt;

import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.springboot.web.FunSpringbootWebProperties;
import com.github.fanzezhen.fun.framework.core.springboot.web.jwt.service.JwtService;
import com.github.fanzezhen.fun.framework.core.verify.repeat.NoRepeat;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * JWT Token生成接口.
 * <p>
 * 提供JWT Token生成服务，需要通过配置开启。
 * 仅在fun.core.verify.api.enabled=true时生效。
 */
@Slf4j
@RestController
@RequestMapping("/fun-core-verify-token")
@ConditionalOnProperty(name = "fun.core.verify.api.enabled", havingValue = "true")
public class FunCoreVerifyTokenApi {
    /**
     * JWT服务.
     */
    private final JwtService jwtService;

    /**
     * Web配置属性.
     */
    @Resource
    private FunSpringbootWebProperties funSpringbootWebProperties;

    /**
     * 构造函数.
     *
     * @param jwtService JWT服务实例
     */
    @Autowired(required = false)
    public FunCoreVerifyTokenApi(final JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * 生成JWT Token.
     * <p>
     * 使用防重放机制，60秒内相同参数只能请求一次。
     *
     * @param code 账号编码
     * @param secretMd5 密钥的MD5值
     * @param timeMillis 时间戳（毫秒）
     * @return 生成的JWT Token字符串
     * @throws ServiceException 请求已过期时抛出
     */
    @NoRepeat(timeout = 60)
    @GetMapping("/generate")
    public String generateJwtToken(@RequestParam(value = "code") @NotBlank final String code,
                                   @RequestParam(value = "secret") @NotBlank final String secretMd5,
                                   @RequestParam(value = "timeMillis") final long timeMillis) {
        FunSpringbootWebProperties.Jwt jwt = funSpringbootWebProperties.getJwt();
        if (jwt.getNetworkDelayMillis() != null &&
                System.currentTimeMillis() - timeMillis > jwt.getNetworkDelayMillis()) {
            throw new ServiceException("请求已过期");
        }
        return jwtService.generateJwtToken(code, secretMd5, timeMillis);
    }

}
