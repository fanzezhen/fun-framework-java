package com.github.fanzezhen.fun.framework.trace.config;

import com.github.fanzezhen.fun.framework.core.springboot.thread.ThreadPoolTaskExecutorRepository;
import com.github.fanzezhen.fun.framework.trace.interceptor.TraceInterceptor;
import com.github.fanzezhen.fun.framework.trace.service.IFunTraceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.trace")
public class FunMpTraceAutoConfiguration {
    @Resource
    private IFunTraceService funTraceService;
    @Resource
    private TraceInterceptor traceInterceptor;

    /**
     * 痕迹入库线程池
     */
    @Bean("funTraceThreadPoolTaskExecutor")
    @ConditionalOnMissingBean(name = "funTraceThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor funTraceThreadPoolTaskExecutor() {
        return ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor("funTraceThreadPoolTaskExecutor", 1, 10);
    }

    @PostConstruct
    public void init() {
        traceInterceptor.setFunTraceService(funTraceService);
    }
}
