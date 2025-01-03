package com.github.fanzezhen.fun.framework.core.thread;

import com.github.fanzezhen.fun.framework.core.property.CommonCoreProperties;
import com.github.fanzezhen.fun.framework.core.property.CommonThreadPoolProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

/**
 * 线程实例助手
 *
 * @author fanzezhen
 * @createTime 2024/2/1 19:26
 * @since 3.1.7
 */
@Slf4j
@Component
public class ThreadInstanceHelper {
    /**
     * 日志跟踪标识
     */
    public static final String TRACE_ID = "traceId";
    @Getter
    private static TaskDecorator taskDecorator;
    @Getter
    private static CommonThreadPoolProperties threadPoolProperties = new CommonThreadPoolProperties();
    @Value("${com.github.fanzezhen.fun.framework.core.thread.decorator-name:sysContextTaskDecorator}")
    private String threadDecoratorName;
    @Resource
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    SysContextTaskDecorator sysContextTaskDecorator() {
        return new SysContextTaskDecorator();
    }

    private static void setTaskDecorator(TaskDecorator taskDecorator) {
        ThreadInstanceHelper.taskDecorator = taskDecorator;
    }

    private static void setThreadPoolProperties(CommonThreadPoolProperties threadPoolProperties) {
        ThreadInstanceHelper.threadPoolProperties = threadPoolProperties;
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
        setThreadPoolProperties(applicationContext.getBean(CommonCoreProperties.class).getThreadPool());
    }
}
