package com.github.fanzezhen.fun.framework.trace.config;

import com.github.fanzezhen.fun.framework.core.springboot.thread.ThreadPoolTaskExecutorRepository;
import com.github.fanzezhen.fun.framework.trace.interceptor.TraceInterceptor;
import com.github.fanzezhen.fun.framework.trace.service.IFunTraceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * MyBatis-Plus 数据追踪自动配置类
 * <p>
 * 负责初始化数据追踪功能所需的核心组件，包括：
 * <ul>
 *   <li>追踪专用线程池 - 异步处理追踪任务，不阻塞主业务</li>
 *   <li>组件关系绑定 - 将追踪服务注入到拦截器中</li>
 *   <li>包扫描 - 自动发现并注册追踪相关的 Bean</li>
 * </ul>
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.trace")
public class FunMpTraceAutoConfiguration {
    @Resource
    private IFunTraceService funTraceService;
    @Resource
    private TraceInterceptor traceInterceptor;

    /**
     * 创建追踪任务专用线程池
     * <p>
     * 配置说明：
     * <ul>
     *   <li>核心线程数：1 - 保证有常驻线程处理追踪任务</li>
     *   <li>最大线程数：10 - 高峰期可扩展到10个线程</li>
     *   <li>线程名前缀：funTraceThreadPoolTaskExecutor</li>
     * </ul>
     * <p>
     * 可通过自定义同名 Bean 覆盖此默认配置。
     *
     * @return 追踪线程池执行器
     */
    @Bean("funTraceThreadPoolTaskExecutor")
    @ConditionalOnMissingBean(name = "funTraceThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor funTraceThreadPoolTaskExecutor() {
        return ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor("funTraceThreadPoolTaskExecutor", 1, 10);
    }

    /**
     * 初始化追踪拦截器
     * <p>
     * 在容器启动后将追踪服务注入到拦截器中，建立组件间的依赖关系。
     */
    @PostConstruct
    public void init() {
        traceInterceptor.setFunTraceService(funTraceService);
    }
}
