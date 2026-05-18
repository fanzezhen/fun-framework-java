package com.github.fanzezhen.fun.framework.security.sa.token.oauth.config;

import com.github.fanzezhen.fun.framework.security.sa.token.oauth.SaOauthManager;
import com.github.fanzezhen.fun.framework.security.sa.token.oauth.SaOauthTemplate;
import com.github.fanzezhen.fun.framework.security.sa.token.oauth.SaOauthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token OAuth2.0 模块 Bean 注入配置类。
 * <p>
 * 自动注入 OAuth2.0 配置对象和模板对象到全局管理器中。
 */
@Configuration
@ConditionalOnClass(SaOauthManager.class)
public class SaOauthBeanInject {

    /**
     * 注入 OAuth2.0 配置对象。
     *
     * @param saOAuth2Config OAuth2.0 配置对象
     */
    @Autowired(required = false)
    public void setSaOAuth2Config(final SaOauthConfig saOAuth2Config) {
        SaOauthManager.setConfig(saOAuth2Config);
    }

    /**
     * 注入 OAuth2.0 模板对象。
     *
     * @param oauthTemplate OAuth2.0 模板对象
     */
    @Autowired(required = false)
    public void setSaOauthTemplate(final SaOauthTemplate oauthTemplate) {
        SaOauthUtil.setOauthTemplate(oauthTemplate);
    }

}
