package com.github.fanzezhen.fun.framework.security.base;

import com.github.fanzezhen.fun.framework.core.model.IUser;

import java.io.Serializable;

/**
 * 登录结果
 */
public interface ILoginResult<K extends Serializable, U extends IUser<K>>{

    U getUser();
    
    String getToken();

    default long getTokenTimeout() {
        return -1L;
    }

    default long getSessionTimeout() {
        return -1L;
    }

    default long getTokenSessionTimeout() {
        return -1L;
    }

    default long getTokenActivityTimeout() {
        return -1L;
    }

    default String getRedirectUrl() {
        return null;
    }

}
