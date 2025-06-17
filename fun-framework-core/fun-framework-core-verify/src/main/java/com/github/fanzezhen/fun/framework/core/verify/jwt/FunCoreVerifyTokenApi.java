package com.github.fanzezhen.fun.framework.core.verify.jwt;

import com.github.fanzezhen.fun.framework.core.verify.FunCoreVerifyProperties;
import com.github.fanzezhen.fun.framework.core.verify.jwt.service.JwtService;
import com.github.fanzezhen.fun.framework.core.verify.repeat.NoRepeat;
import com.github.fanzezhen.fun.framework.core.exception.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import javax.validation.constraints.NotBlank;

/**
 * @author fanzezhen
 */
@Slf4j
@RestController
@RequestMapping("/fun/core/verify/token")
@ConditionalOnProperty(name = "fun.core.verify.api.enabled", havingValue = "true")
public class FunCoreVerifyTokenApi {
    private final JwtService jwtService;
    @Resource
    private FunCoreVerifyProperties funCoreVerifyProperties;

    @Autowired(required = false)
    public FunCoreVerifyTokenApi(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @NoRepeat(timeout = 60)
    @GetMapping("/generate")
    public String generateJwtToken(@RequestParam(value = "code") @NotBlank String code, @RequestParam(value = "secret") @NotBlank String secretMd5, @RequestParam(value = "timeMillis") long timeMillis) {
        FunCoreVerifyProperties.Jwt jwt = funCoreVerifyProperties.getJwt();
        if (jwt.getNetworkDelayMillis() != null && System.currentTimeMillis() - timeMillis > jwt.getNetworkDelayMillis()) {
            throw ExceptionUtil.wrapException("请求已过期");
        }
        return jwtService.generateJwtToken(code, secretMd5, timeMillis);
    }

}
