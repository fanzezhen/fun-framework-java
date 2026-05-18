package com.github.fanzezhen.fun.framework.security.base;

import com.github.fanzezhen.fun.framework.core.model.common.IUser;

import java.io.Serializable;

/**
 * 登录结果接口。
 * <p>
 * 定义登录成功后返回的信息，包括用户信息、令牌、超时时间等。
 *
 * @param <K> 用户ID类型
 * @param <U> 用户实体类型
 */
public interface ILoginResult<K extends Serializable, U extends IUser<K>> {

    /**
     * 获取登录用户信息。
     *
     * @return 用户实体
     */
    U getUser();

    /**
     * 获取访问令牌。
     *
     * @return 令牌字符串
     */
    String getToken();

    /**
     * 获取令牌超时时间（秒）。
     *
     * @return 超时时间，-1 表示永不过期
     */
    default long getTokenTimeout() {
        return -1L;
    }

    /**
     * 获取会话超时时间（秒）。
     *
     * @return 超时时间，-1 表示永不过期
     */
    default long getSessionTimeout() {
        return -1L;
    }

    /**
     * 获取令牌会话超时时间（秒）。
     *
     * @return 超时时间，-1 表示永不过期
     */
    default long getTokenSessionTimeout() {
        return -1L;
    }

    /**
     * 获取令牌活动超时时间（秒）。
     *
     * @return 超时时间，-1 表示永不过期
     */
    default long getTokenActivityTimeout() {
        return -1L;
    }

    /**
     * 获取登录后的重定向URL。
     *
     * @return 重定向URL，null 表示不重定向
     */
    default String getRedirectUrl() {
        return null;
    }

}
