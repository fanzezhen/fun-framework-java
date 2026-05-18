package com.github.fanzezhen.fun.framework.redis;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Fun Framework Redis 自动配置类.
 * <p>
 * 扫描并自动装配 Redis 缓存和分布式锁相关组件。
 * </p>
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.redis")
public class FunRedisAutoConfiguration {
}
