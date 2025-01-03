package com.github.fanzezhen.fun.framework.core.property;

import lombok.NoArgsConstructor;

/**
 * @author zezhen.fan
 * @date 2023/8/14
 */
@NoArgsConstructor
public class CommonThreadPoolProperties {
    private int coreSize = 50;
    private int maxSize = 300;
    private int keepAliveSeconds = 300;
    private int queueCapacity = 500;
    private int circuitBreakerThreshold = 100;

    public CommonThreadPoolProperties(int coreSize, int maxSize, int keepAliveSeconds, int queueCapacity) {
        this.coreSize = coreSize;
        this.maxSize = maxSize;
        this.keepAliveSeconds = keepAliveSeconds;
        this.queueCapacity = queueCapacity;
    }

    public int getCoreSize() {
        return this.coreSize;
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public int getQueueCapacity() {
        return this.queueCapacity;
    }

    public int getKeepAliveSeconds() {
        return this.keepAliveSeconds;
    }

    public int getCircuitBreakerThreshold() {
        return this.circuitBreakerThreshold;
    }

    public void setCoreSize(final int coreSize) {
        this.coreSize = coreSize;
    }

    public void setMaxSize(final int maxSize) {
        this.maxSize = maxSize;
    }

    public void setQueueCapacity(final int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public void setKeepAliveSeconds(final int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public void setCircuitBreakerThreshold(final int circuitBreakerThreshold) {
        this.circuitBreakerThreshold = circuitBreakerThreshold;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof CommonThreadPoolProperties other) {
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getCoreSize() != other.getCoreSize()) {
                return false;
            } else if (this.getMaxSize() != other.getMaxSize()) {
                return false;
            } else if (this.getQueueCapacity() != other.getQueueCapacity()) {
                return false;
            } else if (this.getKeepAliveSeconds() != other.getKeepAliveSeconds()) {
                return false;
            } else {
                return this.getCircuitBreakerThreshold() == other.getCircuitBreakerThreshold();
            }
        } else {
            return false;
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof CommonThreadPoolProperties;
    }

    public int hashCode() {
        int result = 1;
        result = result * 59 + this.getCoreSize();
        result = result * 59 + this.getMaxSize();
        result = result * 59 + this.getQueueCapacity();
        result = result * 59 + this.getKeepAliveSeconds();
        result = result * 59 + this.getCircuitBreakerThreshold();
        return result;
    }

    public String toString() {
        return "CommonThreadPoolProperties(coreSize=" + this.getCoreSize() + ", maxSize=" + this.getMaxSize() + ", queueCapacity=" + this.getQueueCapacity() + ", keepAliveSeconds=" + this.getKeepAliveSeconds() + ", circuitBreakerThreshold=" + this.getCircuitBreakerThreshold() + ")";
    }
}
