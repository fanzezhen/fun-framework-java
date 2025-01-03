package com.github.fanzezhen.fun.framework.core.aspect.jwt;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWT;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.fanzezhen.fun.framework.core.aspect.repeat.NoRepeat;
import com.github.fanzezhen.fun.framework.core.context.SysContextHolder;
import com.github.fanzezhen.fun.framework.core.service.CacheService;
import com.github.fanzezhen.fun.framework.core.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author zezhen.fan
 */
@Slf4j
@RestController
@RequestMapping("/common/token")
public class CommonTokenApi {
    private final CacheService cacheService;

    @Autowired(required = false)
    public CommonTokenApi(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Value("${com.github.fanzezhen.fun.framework.core.jwt.period.seconds:3600}")
    private Integer jwtSeconds;
    @Value("${com.github.fanzezhen.fun.framework.core.jwt.time.delay.seconds:60}")
    private Integer jwtTimeDelaySeconds;
    @Value("${com.github.fanzezhen.fun.framework.core.jwt.account.secret.json:'{\"demoAccount\": {\"secret\": \"demoSecret\",\"tenantId\": \"\"}}'}")
    private String jwtSecretJson;

    @NoRepeat(timeout = 60)
    @GetMapping("/get")
    public String getJwtToken(@RequestParam(value = "appId") @NotBlank String appId, @RequestParam(value = "secret") @NotBlank String secretMd5, @RequestParam(value = "timeMillis") long timeMillis) {
        long jwtTimeDelayMillis = jwtTimeDelaySeconds * 1000L;
        if (System.currentTimeMillis() - timeMillis > jwtTimeDelayMillis){
            throw ExceptionUtil.wrapException("请求已过期");
        }
        JSONObject jwtSecretJsonObject = JSON.parseObject(jwtSecretJson);
        JSONObject authInfoJsonObj = jwtSecretJsonObject.getJSONObject(appId);
        if (MapUtil.isEmpty(authInfoJsonObj)){
            throw ExceptionUtil.wrapException("账户未对接");
        }
        String secret = authInfoJsonObj.getString("secret");
        String md5 = SecureUtil.md5(secret + timeMillis);
        if (!secretMd5.equals(md5)){
            throw ExceptionUtil.wrapException("账户秘钥错误");
        }
        String token = JWT.create().setPayload("appId", appId).setKey(secret.getBytes(StandardCharsets.UTF_8)).sign();
        if (cacheService == null) {
            throw new ServiceException(CoreExceptionEnum.SERVICE_ERROR.getCode(), "缓存未注入");
        }
        cacheService.set(SysContextHolder.getAppId() + SysContextHolder.getTenantId() + "-jwt-" + token, appId, jwtSeconds, TimeUnit.SECONDS);
        return token;
    }

}
