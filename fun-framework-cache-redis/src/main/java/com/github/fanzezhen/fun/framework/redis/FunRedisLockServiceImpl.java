package com.github.fanzezhen.fun.framework.redis;

import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;
import com.github.fanzezhen.fun.framework.core.cache.service.LockService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


/**
 * @author fanzezhen
 */
@Slf4j
@Service
@ConditionalOnBean(RedissonClient.class)
public class FunRedisLockServiceImpl implements LockService {

    @Resource
    RedissonClient redissonClient;

    @Override
    public <T> T lockAndExecute(Supplier<T> supplier, String key, long waitTime, TimeUnit timeUnit) {
        RLock lock = redissonClient.getLock(key);
        try {
            boolean isLock = lock.tryLock(waitTime, timeUnit);
            if (isLock) {
                try {
                    return supplier.get();
                } finally {
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            } else {
                log.info("没有获取到锁，等待时间结束:{}", key);
            }
        } catch (InterruptedException e) {
            log.warn("redis锁错误:{},{}", e, key);
            Thread.currentThread().interrupt(); // 重新中断当前线程
            throw new ServiceException(CoreExceptionEnum.SERVICE_ERROR.getCode(), "redis锁错误" + key + e.getLocalizedMessage());
        }
        throw new ServiceException(CoreExceptionEnum.SERVICE_ERROR.getCode(), "没有获取到锁，等待时间结束" + key + "：" + waitTime + "：" + timeUnit);
    }


}
