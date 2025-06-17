package com.github.fanzezhen.fun.framework.mp.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * MP配置
 *
 * @author fanzezhen
 * @since 3.4.3.1
 */
@Configuration
@ConditionalOnBean({MybatisPlusInterceptor.class, InnerInterceptor.class})
public class FunMpInterceptorAutoConfiguration {
    @Resource
    public MybatisPlusInterceptor mybatisPlusInterceptor;
    @Resource
    public List<InnerInterceptor> innerInterceptors;

    @PostConstruct
    private void init() {
        if (mybatisPlusInterceptor != null && innerInterceptors != null) {
            for (InnerInterceptor innerInterceptor : innerInterceptors) {
                if (mybatisPlusInterceptor.getInterceptors().stream().noneMatch(i -> Objects.equals(i, innerInterceptor))) {
                    mybatisPlusInterceptor.addInnerInterceptor(innerInterceptor);
                }
            }
        }
    }

}
