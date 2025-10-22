package com.github.fanzezhen.fun.framework.core.cache.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;
import com.github.fanzezhen.fun.framework.core.cache.service.CacheService;
import com.github.fanzezhen.fun.framework.core.cache.service.LockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author fanzezhen
 */
@Slf4j
public class FunMemoryLockServiceImpl implements LockService {
    private final CacheService cacheService;

    public FunMemoryLockServiceImpl(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public <T> T lockAndExecute(Supplier<T> supplier, String key, long waitTime, TimeUnit timeUnit) {
        if (cacheService.get(key) != null) {
            try {
                if (waitTime > 0) {
                    Thread.sleep(TimeUnit.MILLISECONDS.convert(waitTime, timeUnit));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 重新中断当前线程
            }
            if (cacheService.get(key) != null) {
                log.warn("没有获取到锁，等待时间结束:{}", key);
                throw new ServiceException(CoreExceptionEnum.SERVICE_ERROR.getCode(), 
                    "没有获取到锁，等待时间结束" + key + "：" + waitTime + "：" + timeUnit);
            }
        }
        try {
            cacheService.set(key, CharSequenceUtil.EMPTY, waitTime, timeUnit);
            return supplier.get();
        } finally {
            cacheService.remove(key);
        }
    }
}
