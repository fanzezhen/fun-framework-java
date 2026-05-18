package com.github.fanzezhen.fun.framework.api.count;

import com.github.fanzezhen.fun.framework.core.model.response.ActionResult;
import com.github.fanzezhen.fun.framework.core.springboot.thread.ThreadPoolTaskExecutorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * API统计模块自动配置类.
 * <p>
 * 提供接口请求统计功能的Bean配置，包括专用线程池和结果解析器。
 * 用户可通过自定义同名Bean覆盖默认实现。
 * </p>
 * <p>
 * 可配置参数：
 * <ul>
 *   <li>fun.api.count.thread-pool.max-size: 线程池最大线程数，默认10</li>
 * </ul>
 * </p>
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.api.count")
public class FunApiCountAutoConfiguration {
    /**
     * API统计线程池最大线程数.
     * <p>
     * 可通过配置项 fun.api.count.thread-pool.max-size 自定义，默认值为10。
     * </p>
     */
    @Value("${fun.api.count.thread-pool.max-size:10}")
    private int maxPoolSize;

    /**
     * API统计专用线程池.
     * <p>
     * 核心线程数1，最大线程数可配置（默认10），避免统计任务占用过多资源影响业务。
     * </p>
     *
     * @return API统计线程池
     */
    @Bean
    @ConditionalOnMissingBean(name = "funApiCountThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor funApiCountThreadPoolTaskExecutor() {
        return ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor(
            "funApiCountThreadPoolTaskExecutor", 1, maxPoolSize);
    }

    /**
     * 默认结果解析器.
     * <p>
     * 自动识别ActionResult并提取data字段，其他类型原样返回。
     * 如需支持自定义统一返回体，可覆盖此Bean。
     *
     * @return 结果解析器
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
