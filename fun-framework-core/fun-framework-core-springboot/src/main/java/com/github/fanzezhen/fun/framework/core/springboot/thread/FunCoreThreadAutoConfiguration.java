package com.github.fanzezhen.fun.framework.core.springboot.thread;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.ttl.TtlRunnable;
import com.github.fanzezhen.fun.framework.core.thread.ThreadPoolExecutorRepository;
import com.github.fanzezhen.fun.framework.core.thread.decorator.ThreadDecorator;
import lombok.SneakyThrows;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;

import java.util.List;
import java.util.concurrent.Future;

/**
 * 线程池模块自动配置类
 * <p>
 * 负责线程池的自动配置和生命周期管理，主要功能包括：
 * <ul>
 *   <li>自动扫描和注册线程装饰器（ThreadDecorator、TaskDecorator）</li>
 *   <li>配置 TTL（TransmittableThreadLocal）任务装饰器</li>
 *   <li>容器销毁时优雅关闭所有线程池</li>
 * </ul>
 * </p>
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.core.thread")
@ConfigurationPropertiesScan("com.github.fanzezhen.fun.framework.core.thread")
public class FunCoreThreadAutoConfiguration implements DisposableBean {

    /**
     * 构造函数
     * <p>
     * 自动收集并注册所有 ThreadDecorator 和 TaskDecorator 实例到线程池仓库中
     * </p>
     *
     * @param threadDecoratorList 线程装饰器列表（可选）
     * @param taskDecoratorList   任务装饰器列表（可选）
     */
    @Autowired(required = false)
    public FunCoreThreadAutoConfiguration(List<ThreadDecorator> threadDecoratorList, List<TaskDecorator> taskDecoratorList) {
        if (threadDecoratorList != null) {
            for (ThreadDecorator threadDecorator : threadDecoratorList) {
                ThreadPoolExecutorRepository.addDecorator(threadDecorator);
                ThreadPoolTaskExecutorRepository.addDecorator(threadDecorator);
            }
        }
        if (taskDecoratorList != null) {
            for (TaskDecorator taskDecorator : taskDecoratorList) {
                if (taskDecorator instanceof ThreadDecorator){
                    continue;
                }
                ThreadPoolExecutorRepository.addDecorator(taskDecorator::decorate);
                ThreadPoolTaskExecutorRepository.addDecorator(taskDecorator, "TaskDecorator");
            }
        }
    }

    /**
     * 创建 TTL（TransmittableThreadLocal）任务装饰器 Bean
     * <p>
     * 用于在线程池任务执行时传递 ThreadLocal 上下文，
     * 可通过配置 {@code fun.springboot.thread.decorator.ttl.disabled=true} 禁用
     * </p>
     *
     * @return TTL 任务装饰器实例
     */
    @Bean
    @ConditionalOnBooleanProperty(
        name = "fun.springboot.thread.decorator.ttl.disabled",
        havingValue = false,
        matchIfMissing = true
    )
    public TaskDecorator ttlRunnableTaskDecorator() {
        return TtlRunnable::get;
    }

    /**
     * 容器销毁时的清理方法
     * <p>
     * 优雅关闭所有已注册的线程池，等待最多60秒
     * </p>
     */
    @SneakyThrows
    @Override
    public void destroy() {
        Future<?> future1 = ThreadUtil.execAsync(() -> ThreadPoolExecutorRepository.destroy(60));
        Future<?> future2 = ThreadUtil.execAsync(() -> ThreadPoolTaskExecutorRepository.destroy(60));
        future1.get();
        future2.get();
    }

}
