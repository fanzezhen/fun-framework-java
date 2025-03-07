package com.github.fanzezhen.fun.framework.core.exception;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.stylefeng.roses.kernel.model.exception.AbstractBaseExceptionEnum;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;

import java.util.Collection;
import java.util.Map;

/**
 * @author zezhen.fan
 */
public class ExceptionUtil {
    private ExceptionUtil() {
    }

    public static ServiceException wrapException(AbstractBaseExceptionEnum exceptionEnum, Object... params) {
        return new ServiceException(exceptionEnum.getCode(), null != params ? String.format(exceptionEnum.getMessage(), params) : exceptionEnum.getMessage());
    }

    public static ServiceException wrapException(String errorMessage) {
        return new ServiceException(CoreExceptionEnum.SERVICE_ERROR.getCode(), errorMessage);
    }

    public static ServiceException wrapException(int code, String errorMessage) {
        return new ServiceException(code, errorMessage);
    }

    public static boolean isEmpty(Object o) {
        return isEmpty(o, false);
    }

    public static boolean isEmpty(Object o, boolean isStrip) {
        if (o == null) {
            return true;
        }
        if (isStrip) {
            if (CharSequenceUtil.isBlank(String.valueOf(o))) {
                return true;
            }
        } else {
            if (CharSequenceUtil.isEmpty(String.valueOf(o))) {
                return true;
            }
        }
        if (o instanceof Map && MapUtil.isEmpty((Map<?, ?>) o)) {
            return true;
        }
        if (o instanceof Collection && CollUtil.isEmpty((Collection<?>) o)) {
            return true;
        }
        if (o instanceof byte[] bytes && bytes.length == 0) {
            return true;
        }
        return o instanceof String[] strings && strings.length == 0;
    }

    public static boolean isNotBlank(Object o) {
        return !isBlank(o);
    }

    public static boolean isBlank(Object o) {
        return isEmpty(o, true);
    }

}
