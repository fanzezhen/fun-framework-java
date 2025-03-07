package com.github.fanzezhen.fun.framework.redis;

import com.github.fanzezhen.fun.framework.core.cache.CacheService;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author zezhen.fan
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
    public void set(String k, String v, long timeout, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(k, v, timeout, timeUnit);
    }

}
