package com.github.fanzezhen.fun.framework.core.config;

import com.github.fanzezhen.fun.framework.core.property.CommonCoreProperties;
import com.github.fanzezhen.fun.framework.core.property.CommonThreadPoolProperties;
import com.github.fanzezhen.fun.framework.core.thread.PoolExecutors;
import com.github.fanzezhen.fun.framework.core.thread.SysContextTaskDecorator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;

/**
 * @author zezhen.fan
 */
@Configuration
@EnableConfigurationProperties({CommonCoreProperties.class})
public class ThreadConfig {
    @Resource
    private CommonCoreProperties commonCoreProperties;
    @Resource(name = "${com.github.fanzezhen.fun.framework.core.thread.decorator-name:sysContextTaskDecorator}")
    private SysContextTaskDecorator sysContextTaskDecorator;

    @Bean
    public ThreadPoolTaskExecutor defaultThreadPoolTaskExecutor() {
        CommonThreadPoolProperties threadPoolProperties = this.commonCoreProperties.getThreadPool();
        int coreSize = threadPoolProperties.getCoreSize();
        int maxSize = threadPoolProperties.getMaxSize();
        int queueCapacity = threadPoolProperties.getQueueCapacity();
        int keepAliveSeconds = threadPoolProperties.getKeepAliveSeconds();
        return PoolExecutors.newThreadPoolTaskExecutor("defaultThreadPoolTaskExecutor", coreSize, maxSize, queueCapacity, keepAliveSeconds);
    }

}
