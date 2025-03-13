package com.github.fanzezhen.fun.framework.trace.config;

import com.github.fanzezhen.fun.framework.core.thread.PoolExecutors;
import com.github.fanzezhen.fun.framework.trace.interceptor.TraceInterceptor;
import com.github.fanzezhen.fun.framework.trace.service.IFunTraceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author fanzezhen
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.trace")
@ServletComponentScan
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
        return PoolExecutors.newThreadPoolTaskExecutor("funTraceThreadPoolTaskExecutor", 1, 10);
    }

    @PostConstruct
    public void init() {
        traceInterceptor.setFunTraceService(funTraceService);
    }
}
