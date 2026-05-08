package com.github.fanzezhen.fun.framework.api.count;

import com.github.fanzezhen.fun.framework.core.model.response.ActionResult;
import com.github.fanzezhen.fun.framework.core.thread.PoolExecutors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * API统计模块自动配置类
 * <p>
 * 提供接口请求统计功能的Bean配置，包括专用线程池和结果解析器。
 * 用户可通过自定义同名Bean覆盖默认实现。
 *
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.api.count")
public class FunApiCountAutoConfiguration {
    /**
     * API统计专用线程池
     * <p>
     * 核心线程数1，最大线程数10，避免统计任务占用过多资源影响业务。
     */
    @Bean
    @ConditionalOnMissingBean(name = "funApiCountThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor funApiCountThreadPoolTaskExecutor() {
        return PoolExecutors.newThreadPoolTaskExecutor("funApiCountThreadPoolTaskExecutor", 1, 10);
    }

    /**
     * 默认结果解析器
     * <p>
     * 自动识别ActionResult并提取data字段，其他类型原样返回。
     * 如需支持自定义统一返回体，可覆盖此Bean。
     */
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
