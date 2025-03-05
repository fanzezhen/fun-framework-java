package com.github.fanzezhen.fun.framework.core.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;
import com.github.fanzezhen.fun.framework.core.service.CacheService;
import com.github.fanzezhen.fun.framework.core.service.LockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author zezhen.fan
 */
@Slf4j
@Order
@Service
@ConditionalOnMissingBean(LockService.class)
public class MemoryLockServiceImpl implements LockService {
    @Resource
    private CacheService memoryCacheServiceImpl;

    @Override
    public <T> T lockKey(String key, long waitTime, TimeUnit timeUnit, Supplier<T> supplier) {
        if (memoryCacheServiceImpl.get(key) != null) {
            try {
                if (waitTime>0) {
                    Thread.sleep(TimeUnit.MILLISECONDS.convert(waitTime, timeUnit));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (memoryCacheServiceImpl.get(key) != null) {
                log.warn("没有获取到锁，等待时间结束:{}", key);
                throw new ServiceException(CoreExceptionEnum.SERVICE_ERROR.getCode(), "没有获取到锁，等待时间结束" + key + "：" + waitTime + "：" + timeUnit);
            }
        }
        try {
            memoryCacheServiceImpl.set(key, CharSequenceUtil.EMPTY, waitTime, timeUnit);
            return supplier.get();
        } finally {
            memoryCacheServiceImpl.remove(key);
        }
    }
}
