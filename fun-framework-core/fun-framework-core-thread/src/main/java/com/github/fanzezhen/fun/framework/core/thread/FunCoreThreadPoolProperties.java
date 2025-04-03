package com.github.fanzezhen.fun.framework.core.thread;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author fanzezhen
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "fun")
public class FunCoreThreadPoolProperties {
    private int coreSize = 50;
    private int maxSize = 300;
    private int keepAliveSeconds = 300;
    private int queueCapacity = 500;
    private int circuitBreakerThreshold = 100;

    public FunCoreThreadPoolProperties(int coreSize, int maxSize, int keepAliveSeconds, int queueCapacity) {
        this.coreSize = coreSize;
        this.maxSize = maxSize;
        this.keepAliveSeconds = keepAliveSeconds;
        this.queueCapacity = queueCapacity;
    }

}
