package com.github.fanzezhen.fun.framework.core.aspect.jwt;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.jwt.JWT;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.fanzezhen.fun.framework.core.context.SysContextHolder;
import com.github.fanzezhen.fun.framework.core.property.CommonCoreProperties;
import com.github.fanzezhen.fun.framework.core.service.CacheService;
import com.github.fanzezhen.fun.framework.core.util.ExceptionUtil;
import com.github.fanzezhen.fun.framework.core.util.SortUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zezhen.fan
 */
@Slf4j
@Aspect
@Component
@ConditionalOnBean(CacheService.class)
public class RequestJwtAop {
    @Resource
    private CommonCoreProperties coreProperties;

    /**
     * 要处理的方法，包名+类名+方法名
     */
    @Pointcut("@annotation(com.github.fanzezhen.fun.framework.core.aspect.jwt.JwtVerify)")
    public void cut() {
    }

    /**
     * 在调用上面 @Pointcut标注的方法前执行以下方法
     *
     * @param joinPoint JoinPoint
     */
    @Before("cut()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        List<Object> argList = new ArrayList<>(joinPoint.getArgs().length);
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
                continue;
            }
            if (!(arg instanceof MultipartFile multipartFile)) {
                argList.add(arg);
            }else {
                argList.add(multipartFile.getName());
            }
        }
        String args = JSON.toJSONString(argList);
        log.info("验证JWT： url={}, Args={}", request.getRequestURL().toString(), args);
        JwtVerify jwtVerify = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(JwtVerify.class);
        String token = SysContextHolder.get(jwtVerify.header());
        if (CharSequenceUtil.isBlank(token)){
            throw ExceptionUtil.wrapException("token不能为空");
        }
        CacheService cacheService = SortUtil.getFirstByOrder(SpringUtil.getBeansOfType(CacheService.class).values());
        String account = cacheService.get(SysContextHolder.getAppId() + SysContextHolder.getTenantId() + "-jwt-" + token);
        if (CharSequenceUtil.isBlank(account)){
            throw ExceptionUtil.wrapException("token is null or not exists");
        }
        JSONObject jwtSecretJsonObj = JSON.parseObject(coreProperties.getJwtSecretJson());
        JSONObject authInfoJson = null;
        try {
            authInfoJson = jwtSecretJsonObj.getJSONObject(account);
        } catch (Exception throwable) {
            log.warn("{}", jwtSecretJsonObj.get(account), throwable);
        }
        if (ExceptionUtil.isBlank(authInfoJson)){
            throw ExceptionUtil.wrapException("账户不存在");
        }
        String secret = authInfoJson.getString("secret");
        if (CharSequenceUtil.isBlank(secret)){
            throw ExceptionUtil.wrapException(account + "应用秘钥未发布");
        }
        // 默认验证HS265的算法
        if (!JWT.of(token).setKey(secret.getBytes(StandardCharsets.UTF_8)).verify()){
            throw ExceptionUtil.wrapException(405, "token验证失败");
        }
        String tenantId = authInfoJson.getString("tenantId");
        if (CharSequenceUtil.isNotEmpty(tenantId)) {
            SysContextHolder.setTenantId(tenantId);
        }
        log.info("验证JWT通过： url={}, Args={}", request.getRequestURL().toString(), args);
    }

}
