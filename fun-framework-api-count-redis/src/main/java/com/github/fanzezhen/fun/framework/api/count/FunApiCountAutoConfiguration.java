package com.github.fanzezhen.fun.framework.api.count;

import com.github.fanzezhen.fun.framework.core.model.response.ActionResult;
import com.github.fanzezhen.fun.framework.core.thread.PoolExecutors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author zezhen.fan
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.api.count")
@ServletComponentScan
public class FunApiCountAutoConfiguration {
    /**
     * 痕迹入库线程池
     */
    @Bean
    @ConditionalOnMissingBean(name = "funApiCountThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor funApiCountThreadPoolTaskExecutor() {
        return PoolExecutors.newThreadPoolTaskExecutor("funApiCountThreadPoolTaskExecutor", 1, 10);
    }
    @Bean
    @ConditionalOnMissingBean
    public FunApiCountResultResolve funApiCountResultResolve() {
        return result -> {
            Object data = result;
            if (data instanceof ActionResult<?> actionResult) {
                data = actionResult.getData();
            }
            return data;
        };
    }
}
