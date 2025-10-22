package com.github.fanzezhen.fun.framework.core.cache;

import com.github.fanzezhen.fun.framework.core.cache.service.CacheService;
import com.github.fanzezhen.fun.framework.core.cache.service.LockService;
import com.github.fanzezhen.fun.framework.core.cache.service.impl.FunMemoryCacheServiceImpl;
import com.github.fanzezhen.fun.framework.core.cache.service.impl.FunMemoryLockServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author fanzezhen
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.core.cache")
@ServletComponentScan
public class FunCoreCacheAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(value = CacheService.class)
    public CacheService funDefaultCacheService() {
        return new FunMemoryCacheServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(value = LockService.class)
    public LockService funDefaultLockService(CacheService cacheService) {
        return new FunMemoryLockServiceImpl(cacheService);
    }
}
