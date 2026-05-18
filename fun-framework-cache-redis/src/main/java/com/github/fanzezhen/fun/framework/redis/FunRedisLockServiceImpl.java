package com.github.fanzezhen.fun.framework.redis;

import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.cache.service.LockService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * 基于Redisson的分布式锁服务实现.
 * <p>
 * 使用Redisson的RLock实现分布式锁功能，支持自动重试和超时控制。
 * 只有在容器中存在RedissonClient Bean时才会启用此实现。
 * </p>
 * <p>
 * <b>线程安全性：</b>通过Redisson内部的锁机制保证多实例环境下的互斥访问。
 * </p>
 */
@Slf4j
@Service
@ConditionalOnBean(RedissonClient.class)
public class FunRedisLockServiceImpl implements LockService {

    /**
     * Redisson 客户端.
     */
    @Resource
    private RedissonClient redissonClient;

    /**
     * 获取锁并执行函数，执行完成后会释放锁，规定时间内获取不到锁则抛出异常.
     *
     * @param supplier 业务函数
     * @param key      锁键
     * @param limit    尝试次数
     * @param waitTime 每次等待时间
     * @param timeUnit 时间单位
     * @param <T>      返回值类型
     * @return 业务函数结果
     * @throws ServiceException 获取锁失败时抛出
     */
    @SneakyThrows
    @Override
    public <T> T lockAndExecute(final FunSupplier<T> supplier,
                                 final String key,
                                 final int limit,
                                 final long waitTime,
                                 final TimeUnit timeUnit) {
        RLock lock = redissonClient.getLock(key);
        for (int i = 1; i <= limit; i++) {
            boolean isLock = lock.tryLock(waitTime, timeUnit);
            if (isLock) {
                log.debug("{} 成功获取到锁，开始执行业务！{}/{}", key, i, limit);
                try {
                    return supplier.call();
                } finally {
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            } else {
                log.debug("{} 没有获取到锁，等待时间结束！{}/{}", key, i, limit);
            }
        }
        throw new ServiceException("没有获取到锁，等待时间结束 " + key + "：" + waitTime + "：" + timeUnit);
    }


}
