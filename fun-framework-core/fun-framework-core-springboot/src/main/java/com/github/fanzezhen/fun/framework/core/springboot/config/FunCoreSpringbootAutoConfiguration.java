package com.github.fanzezhen.fun.framework.core.springboot.config;

import com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Fun Framework Core Springboot 模块自动配置
 * <p>
 * 在 Spring 容器初始化完成后，自动将 MapperFacade 实例注入到 MapperFacadeUtil 工具类中，
 * 使其能够在任何地方通过静态方法调用进行对象映射。
 * </p>
 *
 * @since 4.0.5
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.core.springboot")
public class FunCoreSpringbootAutoConfiguration {

    /**
     * Orika 对象映射门面
     */
    private final MapperFacade mapperFacade;

    /**
     * 构造函数
     *
     * @param mapperFacade Orika 对象映射门面实例（可选）
     */
    public FunCoreSpringbootAutoConfiguration(@Autowired(required = false) MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    /**
     * 初始化方法
     * <p>
     * 在 Spring 容器初始化完成后执行，将 MapperFacade 实例注入到 MapperFacadeUtil 工具类中
     * </p>
     */
    @PostConstruct
    public void init() {
        if (mapperFacade!=null) {
            MapperFacadeUtil.setMapperFacade(mapperFacade);
        }
    }
}
