package com.github.fanzezhen.fun.framework.core.exception;

import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理
 *
 * @author zezhen.fan
 */
@Slf4j
@Component
public class CommonExceptionResolver implements HandlerExceptionResolver {

    private final View defaultErrorJsonView;

    private static final boolean FAST_JSON_VIEW_PRESENT = ClassUtils.isPresent(
        "com.alibaba.fastjson.support.spring.FastJsonJsonView", CommonExceptionResolver.class.getClassLoader());

    public CommonExceptionResolver() throws InstantiationException, IllegalAccessException, ClassNotFoundException,
        NoSuchMethodException, InvocationTargetException {
        if (FAST_JSON_VIEW_PRESENT) {
            defaultErrorJsonView = (View) Class.forName(
                    "com.alibaba.fastjson.support.spring.FastJsonJsonView").getDeclaredConstructor()
                .newInstance();
        } else {
            defaultErrorJsonView = new MappingJackson2JsonView();
        }
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        log.error("请求" + request.getRequestURI() + "发生异常", ex);
        int errorStatus = HttpServletResponse.SC_OK;
        response.setStatus(errorStatus);
        return jsonResponse(ex);
    }

    private ModelAndView jsonResponse(Exception ex) {
        ModelAndView errorView = new ModelAndView();
        Map<String, Object> error = newExceptionResp(ex);
        errorView.setView(defaultErrorJsonView);
        errorView.addAllObjects(error);
        return errorView;
    }

    public Map<String, Object> newExceptionResp(Exception exception) {
        Map<String, Object> error = new HashMap<>(2, 1);
        if (exception instanceof ServiceException serviceException) {
            error.put("msg", serviceException.getErrorMessage());
            error.put("code", serviceException.getCode());
        } else if (exception instanceof IllegalArgumentException) {
            error.put("msg", exception.getMessage());
            error.put("code", CoreExceptionEnum.SERVICE_ERROR.getCode());
        } else {
            error.put("msg", CoreExceptionEnum.SERVICE_ERROR.getMessage());
            error.put("code", CoreExceptionEnum.SERVICE_ERROR.getCode());
        }
        return error;
    }
}
