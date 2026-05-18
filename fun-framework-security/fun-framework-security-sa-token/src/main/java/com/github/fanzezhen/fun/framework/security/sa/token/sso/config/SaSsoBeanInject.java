package com.github.fanzezhen.fun.framework.security.sa.token.sso.config;

import com.github.fanzezhen.fun.framework.security.sa.token.sso.SaSsoManager;
import com.github.fanzezhen.fun.framework.security.sa.token.sso.SaSsoTemplate;
import com.github.fanzezhen.fun.framework.security.sa.token.sso.SaSsoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token SSO 模块 Bean 注入配置类。
 * <p>
 * 自动注入 SSO 配置对象和模板对象到全局管理器中。
 */
@Configuration
@ConditionalOnClass(SaSsoManager.class)
public class SaSsoBeanInject {

    /**
     * 注入 SSO 配置对象。
     *
     * @param saSsoConfig SSO 配置对象
     */
    @Autowired(required = false)
    public void setSaOAuth2Config(final SaSsoConfig saSsoConfig) {
        SaSsoManager.setConfig(saSsoConfig);
    }

    /**
     * 注入 SSO 模板对象。
     *
     * @param ssoTemplate SSO 模板对象
     */
    @Autowired(required = false)
    public void setSaSsoTemplate(final SaSsoTemplate ssoTemplate) {
        SaSsoUtil.setSsoTemplate(ssoTemplate);
    }

}
