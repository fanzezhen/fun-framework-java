package com.github.fanzezhen.fun.framework.core.springboot.config;

import com.github.fanzezhen.fun.framework.core.model.mapper.FieldValueHook;
import com.github.fanzezhen.fun.framework.core.model.mapper.FunObjectMapper;
import com.github.fanzezhen.fun.framework.core.model.mapper.MethodHandleObjectMapper;
import com.github.fanzezhen.fun.framework.core.model.mapper.OrikaObjectMapper;
import com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 对象映射引擎自动配置。
 * <p>
 * 装配可插拔的 {@link FunObjectMapper}，并在容器初始化后注入 {@link MapperFacadeUtil}。
 * 职责单一，与线程池等其它核心装配解耦，便于独立测试与按需覆盖。
 * </p>
 *
 * <p><b>引擎选择（配置项 {@code fun.mapper.engine}）：</b></p>
 * <ul>
 *   <li>{@code method-handle}（默认）：纯 JDK MethodHandle 引擎，无需 {@code --add-opens}</li>
 *   <li>{@code orika}：包装 Orika MapperFacade（存在则复用，否则新建）</li>
 * </ul>
 *
 * <p>子项目注册自定义 {@code FunObjectMapper} bean 即可覆盖框架默认引擎。</p>
 *
 * @since 4.1.0
 */
@Configuration
public class FunMapperAutoConfiguration {

    /**
     * 装配 MethodHandle 引擎（默认引擎）。
     * <p>
     * 收集容器内所有 {@link FieldValueHook}（如 @ProxyField 脱敏 hook）注入引擎。
     * </p>
     *
     * @param hooks 字段写入 hook 提供者
     *
     * @return MethodHandle 映射引擎
     */
    @Bean
    @ConditionalOnMissingBean(FunObjectMapper.class)
    @ConditionalOnProperty(name = "fun.mapper.engine", havingValue = "method-handle", matchIfMissing = true)
    public FunObjectMapper methodHandleObjectMapper(final ObjectProvider<FieldValueHook> hooks) {
        return new MethodHandleObjectMapper(hooks.orderedStream().toList());
    }

    /**
     * 装配 Orika 引擎。
     * <p>
     * 若容器已存在 {@link MapperFacade}（如 proxy-orika 的 ProxyMapperFactory 提供），
     * 则复用之；否则新建默认实例。
     * </p>
     *
     * @param mapperFacade 可选的 Orika 映射门面
     *
     * @return Orika 映射引擎
     */
    @Bean
    @ConditionalOnMissingBean(FunObjectMapper.class)
    @ConditionalOnProperty(name = "fun.mapper.engine", havingValue = "orika")
    public FunObjectMapper orikaObjectMapper(final ObjectProvider<MapperFacade> mapperFacade) {
        MapperFacade facade = mapperFacade.getIfAvailable();
        return facade != null ? new OrikaObjectMapper(facade) : new OrikaObjectMapper();
    }

    /**
     * 将容器中最终生效的映射引擎注入静态工具类 {@link MapperFacadeUtil}。
     * <p>
     * 通过独立 bean 接收 {@link FunObjectMapper} 依赖，避免配置类字段注入自身产出 bean
     * 的时序问题；容器装配完成后即完成静态注入。
     * </p>
     *
     * @param objectMapper 生效的映射引擎（框架默认或子项目自定义）
     *
     * @return 初始化器（无实际业务，仅用于触发注入）
     */
    @Bean
    public MapperFacadeUtilInitializer mapperFacadeUtilInitializer(final FunObjectMapper objectMapper) {
        return new MapperFacadeUtilInitializer(objectMapper);
    }

    /**
     * 静态工具类注入器。构造时将引擎写入 {@link MapperFacadeUtil}。
     */
    public static class MapperFacadeUtilInitializer {
        public MapperFacadeUtilInitializer(final FunObjectMapper objectMapper) {
            MapperFacadeUtil.setObjectMapper(objectMapper);
        }
    }
}
