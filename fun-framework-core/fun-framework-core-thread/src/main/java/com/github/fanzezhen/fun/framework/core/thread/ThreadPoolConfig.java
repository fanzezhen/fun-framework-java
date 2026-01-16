package com.github.fanzezhen.fun.framework.core.thread;

import com.github.fanzezhen.fun.framework.core.thread.decorator.ThreadPoolTaskDecorator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import jakarta.annotation.PostConstruct;

import java.util.List;

/**
 * 线程配置
 *
 * @author fanzezhen
 * @since 3.1.7
 */
@Slf4j
@Configuration
public class ThreadPoolConfig implements DisposableBean {
    @Getter
    private static TaskDecorator taskDecorator;
    private final List<ThreadPoolTaskDecorator> threadPoolTaskDecoratorList;

    @Autowired(required = false)
    public ThreadPoolConfig(List<ThreadPoolTaskDecorator> threadPoolTaskDecoratorList) {
        this.threadPoolTaskDecoratorList = threadPoolTaskDecoratorList;
    }

    @Bean
    @ConditionalOnMissingBean
    ThreadPoolTaskExecutor defaultThreadPoolTaskExecutor() {
        return PoolExecutors.defaultThreadPoolTaskExecutor();
    }

    @PostConstruct
    public void init() {
        try {
            setTaskDecorator(runnable -> {
                for (ThreadPoolTaskDecorator decorator : threadPoolTaskDecoratorList) {
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

    @Override
    public void destroy() {
        PoolExecutors.destroy();
    }
}
