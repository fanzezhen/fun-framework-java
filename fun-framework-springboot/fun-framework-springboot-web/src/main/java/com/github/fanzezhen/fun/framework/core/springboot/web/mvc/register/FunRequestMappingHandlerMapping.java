package com.github.fanzezhen.fun.framework.core.springboot.web.mvc.register;

import com.github.fanzezhen.fun.framework.core.springboot.web.FunSpringbootWebProperties;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.util.PathMatcher;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定义请求映射处理器.
 * <p>
 * 支持根据配置选择性注册或排除指定路径的接口。
 * 通过配置flag和paths参数实现接口的动态注册控制。
 */
@Slf4j
public class FunRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    /**
     * 接口注册配置.
     */
    private final FunSpringbootWebProperties.Register registerProperties;

    /**
     * 构造函数.
     *
     * @param registerProperties 接口注册配置
     */
    public FunRequestMappingHandlerMapping(final FunSpringbootWebProperties.Register registerProperties) {
        this.registerProperties = registerProperties;
    }

    /**
     * 注册处理器方法，支持根据配置选择性注册.
     * <p>
     * <strong>注意:</strong> 创建 {@link RequestMappingInfo} 时，
     * 请使用 {@link #getBuilderConfiguration()} 并在
     * {@link RequestMappingInfo.Builder#options(RequestMappingInfo.BuilderConfiguration)}
     * 上设置选项以匹配此 {@code HandlerMapping} 的配置。
     * 这很重要，例如确保使用基于 {@link PathPattern} 或 {@link PathMatcher} 的匹配。
     *
     * @param handler 处理器的bean名称或处理器实例
     * @param method  要注册的方法
     * @param mapping 与处理器方法关联的映射条件
     */
    @Override
    protected void registerHandlerMethod(@NonNull final Object handler,
                                         @NonNull final Method method,
                                         final RequestMappingInfo mapping) {
        Set<String> directPaths = new HashSet<>();
        // Spring Framework 7.0+ 使用 PathPatternsRequestCondition 替代 PatternsRequestCondition
        PathPatternsRequestCondition pathPatternsCondition = mapping.getPathPatternsCondition();
        if (pathPatternsCondition != null) {
            pathPatternsCondition.getDirectPaths();
            directPaths.addAll(pathPatternsCondition.getDirectPaths());
        }
        if (Boolean.TRUE.equals(registerProperties.getFlag())) {
            for (String directPath : directPaths) {
                if (PatternMatchUtils.simpleMatch(registerProperties.getPaths(), directPath)) {
                    super.registerHandlerMethod(handler, method, mapping);
                    log.info("registerHandlerMethod register {}", mapping);
                    return;
                }
            }
        } else if (Boolean.FALSE.equals(registerProperties.getFlag())) {
            for (String directPath : directPaths) {
                if (PatternMatchUtils.simpleMatch(registerProperties.getPaths(), directPath)) {
                    log.info("registerHandlerMethod skip {}", mapping);
                    return;
                }
            }
            super.registerHandlerMethod(handler, method, mapping);
        }
    }
}
