package com.github.fanzezhen.fun.framework.security.base;

/**
 * 登录参数接口。
 * <p>
 * 定义登录时需要提供的参数，不同登录方式可能需要不同的参数。
 */
public interface ILoginParameter {
    /**
     * 获取登录标识。
     *
     * @return 登录标识，如 username、ticket、手机号等
     */
    String getCode();

}
