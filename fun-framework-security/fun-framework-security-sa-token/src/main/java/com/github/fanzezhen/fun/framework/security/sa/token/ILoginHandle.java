package com.github.fanzezhen.fun.framework.security.sa.token;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.github.fanzezhen.fun.framework.core.exception.ExceptionUtil;
import com.github.fanzezhen.fun.framework.core.model.IUser;
import com.github.fanzezhen.fun.framework.security.base.ILoginParameter;
import com.github.fanzezhen.fun.framework.security.base.ILoginResult;
import com.github.fanzezhen.fun.framework.security.sa.token.enums.SecurityExceptionEnum;
import org.springframework.beans.factory.InitializingBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "unused"})
public interface ILoginHandle<
    K extends Serializable,
    U extends IUser<K>,
    P extends ILoginParameter,
    R extends ILoginResult<K, U>
    > extends InitializingBean {

    List<ILoginHandle<?, ?, ?, ?>> LOGIN_HANDLE_LIST = new ArrayList<>();

    /**
     * 是否支持指定的登录方式
     */
    boolean isSupport(Object mode);

    /**
     * 验证登录用户
     */
    U verify(P parameter);

    /**
     * 制作登录结果
     */
    R makeLoginResult(U user, SaTokenInfo tokenInfo);

    /**
     * 登录
     */
    default R doLogin(P parameter) {
        U user = verify(parameter);
        if (user == null) {
            throw ExceptionUtil.wrapException(SecurityExceptionEnum.LOGIN_FAILED_USER_VERIFY_ERROR);
        }
        StpUtil.login(user.getLoginCode());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return makeLoginResult(user, tokenInfo);
    }

    /**
     * 退出登录
     */
    default Object doLogout() {
        StpUtil.logout();
        return null;
    }

    /**
     * 注册到工厂
     */
    @Override
    default void afterPropertiesSet() {
        LOGIN_HANDLE_LIST.add(this);
    }

    static <T extends ILoginHandle<?, ?, ?, ?>> T getLoginHandle(Object mode) {
        for (ILoginHandle<?, ?, ?, ?> loginHandle : LOGIN_HANDLE_LIST) {
            if (loginHandle.isSupport(mode)) {
                return (T) loginHandle;
            }
        }
        throw ExceptionUtil.wrapException(SecurityExceptionEnum.LOGIN_MODEL_UNSUPPORTED, mode);
    }
}
