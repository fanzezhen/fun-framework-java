package com.github.fanzezhen.fun.framework.redis;

import com.github.fanzezhen.fun.framework.core.cache.service.CacheService;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author fanzezhen
 */
@Order
@Service
public class FunRedisCacheServiceImpl implements CacheService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String get(String k) {
        return stringRedisTemplate.opsForValue().get(k);
    }

    @Override
    public void put(String k, String v, long timeout) {
        stringRedisTemplate.opsForValue().set(k, v, timeout, TimeUnit.MICROSECONDS);
    }

    @Override
    public void remove(String k) {
        stringRedisTemplate.delete(k);
    }

    @Override
    public Boolean setIfAbsent(String k, Serializable v, long timeout, TimeUnit timeUnit) {
       return stringRedisTemplate.opsForValue().setIfAbsent(k, String.valueOf(v), timeout, timeUnit);
    }

}
