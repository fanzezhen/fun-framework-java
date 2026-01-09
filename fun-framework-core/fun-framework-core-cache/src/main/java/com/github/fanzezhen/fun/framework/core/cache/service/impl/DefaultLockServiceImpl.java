package com.github.fanzezhen.fun.framework.core.cache.service.impl;

import cn.hutool.core.date.DateUtil;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.cache.service.CacheService;
import com.github.fanzezhen.fun.framework.core.cache.service.LockService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author fanzezhen
 */
@Slf4j
public record DefaultLockServiceImpl(CacheService cacheService) implements LockService {

    /**
     * 获取锁并执行函数，执行完成后会释放锁，规定时间内获取不到锁则抛出异常
     *
     * @param supplier 业务函数
     * @param key      键
     * @param limit    尝试次数
     * @param waitTime 每次等待时间
     * @param timeUnit 时间单位
     *
     * @return 业务函数结果
     */
    @SneakyThrows
    @Override
    public <T> T lockAndExecute(FunSupplier<T> supplier, String key, int limit, long waitTime, TimeUnit timeUnit) {
        for (int i = 1; i <= limit; i++) {
            boolean isLock = cacheService.setIfAbsent(key, DateUtil.now(), 1, TimeUnit.HOURS);
            if (isLock) {
                log.debug("{} 成功获取到锁，开始执行业务！{}/{}", key, i, limit);
                try {
                    return supplier.call();
                } finally {
                    cacheService.remove(key);
                }
            } else {
                log.debug("{} 没有获取到锁，等待时间结束！{}/{}", key, i, limit);
            }
        }
        throw new ServiceException("没有获取到锁，等待时间结束 " + key + "：" + waitTime + "：" + timeUnit);
    }
}
