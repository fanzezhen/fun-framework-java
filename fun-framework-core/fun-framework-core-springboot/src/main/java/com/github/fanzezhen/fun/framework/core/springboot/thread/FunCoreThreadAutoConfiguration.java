package com.github.fanzezhen.fun.framework.core.springboot.thread;

import cn.hutool.core.thread.ThreadUtil;
import com.github.fanzezhen.fun.framework.core.thread.ThreadPoolExecutorRepository;
import com.github.fanzezhen.fun.framework.core.thread.decorator.ThreadPoolTaskDecorator;
import lombok.SneakyThrows;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.Future;

/**
 * 线程池模块自动配置类
 * <p>
 * 加载默认线程池（PoolExecutors）和线程装饰器（ThreadPoolTaskDecorator）等组件，
 * 支持上下文传递、线程池监控等功能。
 *
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.core.thread")
@ConfigurationPropertiesScan("com.github.fanzezhen.fun.framework.core.thread")
public class FunCoreThreadAutoConfiguration implements DisposableBean {

    @Autowired(required = false)
    public FunCoreThreadAutoConfiguration(List<ThreadPoolTaskDecorator> threadPoolTaskDecoratorList) {
        if (threadPoolTaskDecoratorList!=null){
            for (ThreadPoolTaskDecorator threadPoolTaskDecorator : threadPoolTaskDecoratorList) {
                ThreadPoolExecutorRepository.addDecorator(threadPoolTaskDecorator);
                ThreadPoolTaskExecutorRepository.addDecorator(threadPoolTaskDecorator);
            }
        }
    }

    @SneakyThrows
    @Override
    public void destroy() {
        Future<?> future1 = ThreadUtil.execAsync(() -> ThreadPoolExecutorRepository.destroy(60));
        Future<?> future2 = ThreadUtil.execAsync(() -> ThreadPoolTaskExecutorRepository.destroy(60));
        future1.get();
        future2.get();
    }
    
}
