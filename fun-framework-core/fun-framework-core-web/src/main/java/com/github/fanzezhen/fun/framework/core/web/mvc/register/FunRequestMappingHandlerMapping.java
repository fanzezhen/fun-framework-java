package com.github.fanzezhen.fun.framework.core.web.mvc.register;

import com.github.fanzezhen.fun.framework.core.web.FunCoreWebProperties;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
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
 * 接口注册
 *
 * @author fanzezhen
 */
@Slf4j
public class FunRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    private final FunCoreWebProperties.Register registerProperties;

    public FunRequestMappingHandlerMapping(FunCoreWebProperties.Register registerProperties) {
        this.registerProperties = registerProperties;
    }

    /**
     * {@inheritDoc}
     * <p><strong>Note:</strong> To create the {@link RequestMappingInfo},
     * please use {@link #getBuilderConfiguration()} and set the options on
     * {@link RequestMappingInfo.Builder#options(RequestMappingInfo.BuilderConfiguration)}
     * to match how this {@code HandlerMapping} is configured. This
     * is important for example to ensure use of
     * {@link PathPattern} or
     * {@link PathMatcher} based matching.
     *
     * @param handler the bean name of the handler or the handler instance
     * @param method  the method to register
     * @param mapping the mapping conditions associated with the handler method
     */
    @Override
    protected void registerHandlerMethod(@NotNull Object handler, @NotNull Method method, RequestMappingInfo mapping) {
        Set<String> directPaths = new HashSet<>(mapping.getPatternsCondition().getDirectPaths());
        PathPatternsRequestCondition pathPatternsCondition = mapping.getPathPatternsCondition();
        if (pathPatternsCondition != null && pathPatternsCondition.getDirectPaths() != null) {
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
