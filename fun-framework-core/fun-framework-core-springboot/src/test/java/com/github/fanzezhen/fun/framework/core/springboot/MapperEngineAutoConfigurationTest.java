package com.github.fanzezhen.fun.framework.core.springboot;

import com.github.fanzezhen.fun.framework.core.model.mapper.FunObjectMapper;
import com.github.fanzezhen.fun.framework.core.model.mapper.MethodHandleObjectMapper;
import com.github.fanzezhen.fun.framework.core.model.mapper.OrikaObjectMapper;
import com.github.fanzezhen.fun.framework.core.springboot.config.FunMapperAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 映射引擎自动装配测试。
 * <p>
 * 验证：默认装配 MethodHandle 引擎、fun.mapper.engine=orika 切换 Orika 引擎、
 * 子项目自定义 FunObjectMapper bean 覆盖框架默认。
 * </p>
 *
 * @since 4.1.0
 */
class MapperEngineAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FunMapperAutoConfiguration.class));

    @Test
    void testDefaultEngineIsMethodHandle() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(FunObjectMapper.class);
            assertThat(context.getBean(FunObjectMapper.class)).isInstanceOf(MethodHandleObjectMapper.class);
        });
    }

    @Test
    void testOrikaEngineByProperty() {
        runner.withPropertyValues("fun.mapper.engine=orika").run(context -> {
            assertThat(context).hasSingleBean(FunObjectMapper.class);
            assertThat(context.getBean(FunObjectMapper.class)).isInstanceOf(OrikaObjectMapper.class);
        });
    }

    @Test
    void testCustomMapperOverridesDefault() {
        runner.withUserConfiguration(CustomMapperConfig.class).run(context -> {
            assertThat(context).hasSingleBean(FunObjectMapper.class);
            assertThat(context.getBean(FunObjectMapper.class)).isSameAs(CustomMapperConfig.CUSTOM);
        });
    }

    @Configuration
    static class CustomMapperConfig {
        static final FunObjectMapper CUSTOM = new MethodHandleObjectMapper();

        @Bean
        FunObjectMapper customObjectMapper() {
            return CUSTOM;
        }
    }
}
