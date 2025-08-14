package com.github.fanzezhen.fun.framework.security.sa.token.oauth.config;

import com.github.fanzezhen.fun.framework.security.sa.token.oauth.SaOauthManager;
import com.github.fanzezhen.fun.framework.security.sa.token.oauth.SaOauthTemplate;
import com.github.fanzezhen.fun.framework.security.sa.token.oauth.SaOauthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * 注入 Sa-Token-Oauth 所需要的Bean
 */
@Configuration
@ConditionalOnClass(SaOauthManager.class)
public class SaOauthBeanInject {

    /**
     * 注入 Sa-Token-Oauth 配置Bean
     *
     * @param saOAuth2Config 配置对象
     */
    @Autowired(required = false)
    public void setSaOAuth2Config(SaOauthConfig saOAuth2Config) {
        SaOauthManager.setConfig(saOAuth2Config);
    }

    /**
     * 注入 Sa-Token-Oauth 单点登录模块 Bean
     *
     * @param oauthTemplate oauthTemplate对象
     */
    @Autowired(required = false)
    public void setSaOauthTemplate(SaOauthTemplate oauthTemplate) {
        SaOauthUtil.setOauthTemplate(oauthTemplate);
    }

}
