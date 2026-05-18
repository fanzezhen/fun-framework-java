package com.github.fanzezhen.fun.framework.redis;

import com.github.fanzezhen.fun.framework.core.cache.service.CacheService;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.io.Serializable;
import java.time.Duration;

/**
 * Redis缓存服务实现类.
 * <p>
 * 基于StringRedisTemplate实现的缓存服务，所有value以String形式存储。
 * 使用@Order注解提升Bean加载优先级，优先使用Redis缓存而非内存缓存。
 * </p>
 */
@Order
@Service
public class FunRedisCacheServiceImpl implements CacheService {
    /**
     * Spring Data Redis 字符串操作模板.
     */
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取缓存值.
     *
     * @param k 缓存键
     * @return 缓存值
     */
    @Override
    public String get(final String k) {
        return stringRedisTemplate.opsForValue().get(k);
    }

    /**
     * 存入缓存.
     *
     * @param k 缓存键
     * @param v 缓存值
     * @param timeout 超时时间（微秒）
     */
    @Override
    public void put(final String k, final String v, final long timeout) {
        stringRedisTemplate.opsForValue().set(k, v, Duration.ofNanos(timeout * 1000));
    }

    /**
     * 移除缓存.
     *
     * @param k 缓存键
     */
    @Override
    public void remove(final String k) {
        stringRedisTemplate.delete(k);
    }

    /**
     * 仅当键不存在时设置值.
     *
     * @param k 缓存键
     * @param v 缓存值
     * @param timeout 超时时间（Duration对象）
     * @return 是否设置成功
     */
    @Override
    public Boolean setIfAbsent(final String k, final Serializable v, final Duration timeout) {
       return stringRedisTemplate.opsForValue().setIfAbsent(k, String.valueOf(v), timeout);
    }

}
