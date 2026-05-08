package com.github.fanzezhen.fun.framework.mp.config;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import java.util.List;
import java.util.Objects;

/**
 * MP配置
 *
 * @since 3.4.3.1
 */
@Slf4j
@Configuration
@ConditionalOnBean({MybatisPlusInterceptor.class})
public class FunMpInterceptorAutoConfiguration {
    @Resource
    public MybatisPlusInterceptor mybatisPlusInterceptor;
    public final List<InnerInterceptor> innerInterceptors;

    public FunMpInterceptorAutoConfiguration(@Autowired(required = false) List<InnerInterceptor> innerInterceptors) {
        this.innerInterceptors = innerInterceptors;
    }

    @PostConstruct
    private void init() {
        if (mybatisPlusInterceptor != null && CollUtil.isNotEmpty(innerInterceptors)) {
            for (InnerInterceptor innerInterceptor : innerInterceptors) {
                if (mybatisPlusInterceptor.getInterceptors().stream().noneMatch(
                    i -> Objects.equals(i, innerInterceptor))) {
                    mybatisPlusInterceptor.addInnerInterceptor(innerInterceptor);
                }
            }
        }
        if (mybatisPlusInterceptor == null || mybatisPlusInterceptor.getInterceptors().stream().noneMatch(PaginationInnerInterceptor.class::isInstance)) {
            log.warn("""
                
                ================================================================================
                ⚠️  警告: 未检测到 PaginationInnerInterceptor 配置!
                ================================================================================
                
                【问题】分页功能将不可用,分页查询将返回空数据或全量数据
                
                【解决方案】请在项目中添加以下配置:
                
                    @Configuration
                    public class MybatisPlusConfig {
                        @Bean
                        public InnerInterceptor paginationInnerInterceptor() {
                            return new PaginationInnerInterceptor(DbType.MYSQL);
                        }
                    }
                
                【详细文档】
                - README: https://github.com/fanzezhen/fun-framework-java/blob/master/fun-framework-data/fun-framework-data-mp/fun-framework-data-mp-starter/README.md#2-%EF%B8%8F-%E5%BF%85%E9%9C%80%E9%85%8D%E7%BD%AE%E9%87%8D%E8%A6%81
                - 示例代码: https://github.com/fanzezhen/fun-framework-java/blob/master/fun-framework-data/fun-framework-data-mp/fun-framework-data-mp-starter/src/test/java/com/github/fanzezhen/fun/framework/mp/example/MybatisPlusExampleConfig.java
                
                ================================================================================
                """);
        }
    }

}
