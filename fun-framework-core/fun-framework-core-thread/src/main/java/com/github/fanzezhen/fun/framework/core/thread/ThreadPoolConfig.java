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
 * 线程池配置类
 * <p>
 * 自动注册默认线程池，并组合所有ThreadPoolTaskDecorator实现类形成装饰器链，
 * 用于实现上下文传递、日志增强、性能监控等横切关注点。
 * <p>
 * <b>生命周期：</b>实现DisposableBean，在容器销毁时自动关闭线程池
 *
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
