package com.github.fanzezhen.fun.framework.security.sa.token.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.filter.SaFilter;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.alibaba.fastjson2.JSON;
import com.github.fanzezhen.fun.framework.core.model.response.ActionResult;
import com.github.fanzezhen.fun.framework.core.model.response.ErrorInfo;
import com.github.fanzezhen.fun.framework.security.base.FunSecurityFacade;
import com.github.fanzezhen.fun.framework.security.base.FunSpringSecurityProperties;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ServletComponentScan
@AutoConfigureAfter(FunSpringSecurityProperties.class)
public class FunSaTokenConfiguration {

    /**
     * 权限门面类，用于调用权限相关的业务逻辑。
     */
    @Resource
    private FunSecurityFacade funPermissionFacade;

    /**
     * 注册 Sa-Token 全局过滤器
     */
    @Bean
    @ConditionalOnMissingBean(SaFilter.class)
    public SaServletFilter defaultSaServletFilter(FunSpringSecurityProperties funSpringSecurityProperties) {
        return new SaServletFilter()
            // 拦截地址
            .addInclude("/**").addExclude("/favicon.ico", "/actuator/**")
            // 鉴权方法：每次访问进入
            .setAuth(obj -> {
                // 登录校验 -- 拦截所有路由
                SaRouter.match("/**")
                    .notMatch(funSpringSecurityProperties.getIgnoreUriArr())
                    .check(r -> {
                        StpUtil.checkLogin();
                        // 登录验证通过，执行登录验证的回调
                        funPermissionFacade.callbackForCheckLogin(StpUtil.getLoginId());
                    });
                // 遍历校验规则，依次鉴权
                List<String> needManageUriList = funPermissionFacade.needManageUriList(StpUtil.getLoginDeviceType());
                needManageUriList.forEach(uri -> SaRouter.match(uri, () -> StpUtil.checkPermission(SaHolder.getRequest().getRequestPath())));
            }).setError(e -> {
                SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
                if (e instanceof ServiceException serviceException) {
                    return JSON.toJSONString(ActionResult.failed(serviceException));
                }
                if (e instanceof NotPermissionException) {
                    return JSON.toJSONString(ActionResult.failed(new ErrorInfo(403, "权限不足")));
                }
                return JSON.toJSONString(ActionResult.failed("权限验证失败"));
            });
    }

}
