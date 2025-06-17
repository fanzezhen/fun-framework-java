package com.github.fanzezhen.fun.framework.core.thread;

import com.github.fanzezhen.fun.framework.core.thread.decorator.ThreadPoolTaskDecorator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 线程配置
 *
 * @author fanzezhen
 * @since 3.1.7
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {
    @Getter
    private static TaskDecorator taskDecorator;
    @Resource
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    ThreadPoolTaskExecutor defaultThreadPoolTaskExecutor() {
        return PoolExecutors.defaultThreadPoolTaskExecutor();
    }

    @PostConstruct
    public void init() {
        try {
            Map<String, ThreadPoolTaskDecorator> beanMap = applicationContext.getBeansOfType(ThreadPoolTaskDecorator.class);
            setTaskDecorator(runnable -> {
                for (ThreadPoolTaskDecorator decorator : beanMap.values()) {
                    runnable = decorator.decorate(runnable);
                }
                return runnable;
            });
        } catch (Exception exception) {
            log.warn("自动加载线程装饰器失败", exception);
        }
    }

    private static void setTaskDecorator(TaskDecorator taskDecorator) {
        ThreadPoolConfig.taskDecorator = taskDecorator;
    }
}
