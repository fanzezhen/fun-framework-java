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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnBean(UserDetailsService.class)
public class FunSpringSecurityConfig {
    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private FunSpringSecurityProperties funSpringSecurityProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        @Autowired(required = false) CasAuthenticationFilter casAuthenticationFilter) throws Exception {
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

    @Bean
    @ConditionalOnProperty(value = "fun.security.cas.service-url")
    public CasAuthenticationFilter casAuthenticationFilter() {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    @Bean
    @ConditionalOnProperty(value = "fun.security.cas.service-url")
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(casAuthenticationProvider()));
    }

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

    @Bean
    @ConditionalOnProperty(value = "fun.security.cas.service-url")
    public ServiceProperties casServiceProperties() {
        ServiceProperties sp = new ServiceProperties();
        sp.setService(funSpringSecurityProperties.getCas().getServiceUrl());
        sp.setAuthenticateAllArtifacts(true);
        return sp;
    }

    @Bean
    @ConditionalOnProperty(value = "fun.security.cas.service-url")
    public TicketValidator casTicketValidator() {
        return new Cas30ServiceTicketValidator(funSpringSecurityProperties.getCas().getServerUrlPrefix());
    }

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 使用 BCrypt 编码器
    }

}
