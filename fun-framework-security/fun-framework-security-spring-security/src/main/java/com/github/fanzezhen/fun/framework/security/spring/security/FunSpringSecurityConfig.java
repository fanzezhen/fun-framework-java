package com.github.fanzezhen.fun.framework.security.spring.security;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.security.base.FunSpringSecurityProperties;
import jakarta.annotation.Resource;
import org.apereo.cas.client.validation.Cas30ServiceTicketValidator;
import org.apereo.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

/**
 * Spring Security 配置类。
 * <p>
 * 配置 Spring Security 的安全过滤链、认证管理器和密码编码器等。
 * 支持表单登录、CAS 单点登录、OAuth2.0 登录等多种认证方式。
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnBean(UserDetailsService.class)
public class FunSpringSecurityConfig {
    /**
     * 用户详情服务。
     */
    @Resource
    private UserDetailsService userDetailsService;

    /**
     * 安全配置属性。
     */
    @Resource
    private FunSpringSecurityProperties funSpringSecurityProperties;

    /**
     * 配置安全过滤链。
     *
     * @param http                     HTTP安全配置对象
     * @param casAuthenticationFilter CAS认证过滤器（可选）
     * @return 安全过滤链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
        final HttpSecurity http,
        @Autowired(required = false) final CasAuthenticationFilter casAuthenticationFilter) throws Exception {
        http
            .authorizeHttpRequests(requests -> requests
                .requestMatchers(funSpringSecurityProperties.getIgnoreUriArr()).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.loginPage(funSpringSecurityProperties.getLoginPage()).permitAll())
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .httpBasic(Customizer.withDefaults()) // 启用 HTTP Basic 认证
            .csrf(AbstractHttpConfigurer::disable);
        if (casAuthenticationFilter != null) {
            http.addFilterBefore(casAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }
        if (CharSequenceUtil.isNotEmpty(funSpringSecurityProperties.getOauth().getServiceUrl())) {
            http.oauth2Login(AbstractAuthenticationFilterConfigurer::permitAll);
        }
        return http.build();
    }

    /**
     * 配置 CAS 认证过滤器。
     * <p>
     * 仅在配置了 CAS 服务地址时生效。
     *
     * @return CAS 认证过滤器
     */
    @Bean
    @ConditionalOnProperty(value = "fun.security.cas.service-url")
    public CasAuthenticationFilter casAuthenticationFilter() {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    /**
     * 配置认证管理器。
     * <p>
     * 仅在配置了 CAS 服务地址时生效。
     *
     * @return 认证管理器
     */
    @Bean
    @ConditionalOnProperty(value = "fun.security.cas.service-url")
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(casAuthenticationProvider()));
    }

    /**
     * 配置 CAS 认证提供者。
     * <p>
     * 仅在配置了 CAS 服务地址时生效。
     *
     * @return CAS 认证提供者
     */
    @Bean
    @ConditionalOnProperty(value = "fun.security.cas.service-url")
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setServiceProperties(casServiceProperties());
        provider.setTicketValidator(casTicketValidator());
        provider.setUserDetailsService(userDetailsService);
        provider.setKey(funSpringSecurityProperties.getCas().getAuthenticationProviderKey());
        return provider;
    }

    /**
     * 配置 CAS 服务属性。
     * <p>
     * 仅在配置了 CAS 服务地址时生效。
     *
     * @return CAS 服务属性
     */
    @Bean
    @ConditionalOnProperty(value = "fun.security.cas.service-url")
    public ServiceProperties casServiceProperties() {
        ServiceProperties sp = new ServiceProperties();
        sp.setService(funSpringSecurityProperties.getCas().getServiceUrl());
        sp.setAuthenticateAllArtifacts(true);
        return sp;
    }

    /**
     * 配置 CAS Ticket 验证器。
     * <p>
     * 仅在配置了 CAS 服务地址时生效。
     *
     * @return CAS Ticket 验证器
     */
    @Bean
    @ConditionalOnProperty(value = "fun.security.cas.service-url")
    public TicketValidator casTicketValidator() {
        return new Cas30ServiceTicketValidator(funSpringSecurityProperties.getCas().getServerUrlPrefix());
    }

    /**
     * 配置密码编码器。
     * <p>
     * 使用 BCrypt 编码器对密码进行加密。
     *
     * @return 密码编码器
     */
    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
