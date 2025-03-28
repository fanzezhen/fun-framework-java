package com.github.fanzezhen.fun.framework.core.web.mvc;

/**
 * @author fanzezhen
 * @since 3.4.3.5
 */
public interface ResponseBodyWrapper {
    /**
     * 是否已经包装过了
     */
    boolean isWrapped(Object data);

    /**
     * 包装
     */
    Object wrap(Object data);
}
