package com.github.fanzezhen.fun.framework.core.log.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zezhen.fan
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.core.log")
@EnableFeignClients(basePackages = {"com.github.fanzezhen.fun.framework.core.log"})
public class FunCoreLogAutoConfiguration {
    /**
     * 日志跟踪标识
     */
    public static final String TRACE_ID = "traceId";
}
