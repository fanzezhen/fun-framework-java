package com.github.fanzezhen.fun.framework.core.springboot.config;

import com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * MapperFacadeUtil 自动配置
 * <p>
 * 在 Spring 容器初始化完成后，自动将 MapperFacade 实例注入到 MapperFacadeUtil 工具类中，
 * 使其能够在任何地方通过静态方法调用进行对象映射。
 * </p>
 *
 * @author fanzezhen
 * @since 4.0.5
 */
@Configuration
public class FunCoreSpringbootAutoConfiguration {

    private final MapperFacade mapperFacade;

    public FunCoreSpringbootAutoConfiguration(@Autowired(required = false) MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @PostConstruct
    public void init() {
        if (mapperFacade!=null) {
            MapperFacadeUtil.setMapperFacade(mapperFacade);
        }
    }
}
