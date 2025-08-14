package com.github.fanzezhen.fun.framework.security.sa.token.sso.config;

import com.github.fanzezhen.fun.framework.security.sa.token.sso.SaSsoManager;
import com.github.fanzezhen.fun.framework.security.sa.token.sso.SaSsoTemplate;
import com.github.fanzezhen.fun.framework.security.sa.token.sso.SaSsoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * 注入 Sa-Token-SSO 所需要的Bean
 */
@Configuration
@ConditionalOnClass(SaSsoManager.class)
public class SaSsoBeanInject {

    /**
     * 注入 Sa-Token-SSO 配置Bean
     *
     * @param saSsoConfig 配置对象
     */
    @Autowired(required = false)
    public void setSaOAuth2Config(SaSsoConfig saSsoConfig) {
        SaSsoManager.setConfig(saSsoConfig);
    }

    /**
     * 注入 Sa-Token-SSO 单点登录模块 Bean
     *
     * @param ssoTemplate saSsoTemplate对象
     */
    @Autowired(required = false)
    public void setSaSsoTemplate(SaSsoTemplate ssoTemplate) {
        SaSsoUtil.setSsoTemplate(ssoTemplate);
    }

}
