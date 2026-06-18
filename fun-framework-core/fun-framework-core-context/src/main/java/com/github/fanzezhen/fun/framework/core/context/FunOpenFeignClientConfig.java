package com.github.fanzezhen.fun.framework.core.context;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenFeign客户端配置类.
 * <p>
 * 提供请求拦截器，在Feign调用时自动将上下文信息添加到请求头中，
 * 实现微服务间的上下文透传。
 */
@Slf4j
@Configuration
@ConditionalOnClass(name = {"org.springframework.cloud.openfeign.EnableFeignClients"})
public class FunOpenFeignClientConfig {
    /**
     * 系统上下文拦截器Bean.
     * <p>
     * 在Feign请求发送前，自动将上下文信息添加到请求头中。
     *
     * @return 请求拦截器
     */
    @Bean
    public RequestInterceptor systemContextInterceptor() {
        return requestTemplate -> {
            List<Pair<String, String>> pairs = ContextHolder.toHeaders();
            if (CollUtil.isNotEmpty(pairs)) {
                for (Pair<String, String> pair : pairs) {
                    if (log.isDebugEnabled()) {
                        log.debug("add header:{},value:{} to feign request", pair.getKey(), pair.getValue());
                    }
                    requestTemplate.header(pair.getKey(), pair.getValue());
                }
            }
        };
    }
}
