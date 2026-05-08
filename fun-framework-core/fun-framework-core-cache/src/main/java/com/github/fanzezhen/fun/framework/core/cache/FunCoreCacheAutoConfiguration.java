package com.github.fanzezhen.fun.framework.core.cache;

import com.github.fanzezhen.fun.framework.core.cache.service.CacheService;
import com.github.fanzezhen.fun.framework.core.cache.service.LockService;
import com.github.fanzezhen.fun.framework.core.cache.service.impl.FunMemoryCacheServiceImpl;
import com.github.fanzezhen.fun.framework.core.cache.service.impl.DefaultLockServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存模块自动配置类
 * <p>
 * 提供默认的缓存和分布式锁实现：
 * <ul>
 *   <li>当容器中不存在CacheService时，注册基于内存的FunMemoryCacheServiceImpl</li>
 *   <li>当容器中存在CacheService但不存在LockService时，注册基于缓存实现的DefaultLockServiceImpl</li>
 * </ul>
 * 业务系统可通过注入自定义的CacheService（如Redis实现）覆盖默认配置。
 *
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.core.cache")
public class FunCoreCacheAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(value = CacheService.class)
    public CacheService funDefaultCacheService() {
        return new FunMemoryCacheServiceImpl();
    }

    @Bean
    @ConditionalOnBean(value = CacheService.class)
    @ConditionalOnMissingBean(value = LockService.class)
    public LockService funDefaultLockService(CacheService cacheService) {
        return new DefaultLockServiceImpl(cacheService);
    }
}
