package com.github.fanzezhen.fun.framework.redis;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zezhen.fan
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.redis")
@ServletComponentScan
public class FunRedisAutoConfiguration {
}
