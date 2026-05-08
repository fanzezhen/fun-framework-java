package com.github.fanzezhen.fun.framework.security.sa.token;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.model.common.IUser;
import com.github.fanzezhen.fun.framework.security.base.ILoginParameter;
import com.github.fanzezhen.fun.framework.security.base.ILoginResult;
import com.github.fanzezhen.fun.framework.security.sa.token.enums.SecurityExceptionEnum;
import org.springframework.beans.factory.InitializingBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录处理器接口（策略模式）
 * <p>
 * 支持多种登录方式（用户名密码、短信验证码、OAuth2等），通过isSupport方法路由到对应的处理器。
 * 实现类会在Spring初始化后自动注册到LOGIN_HANDLE_LIST，运行时通过getLoginHandle查找匹配的处理器。
 * <p>
 * <b>泛型说明：</b>
 * <ul>
 *   <li>K: 用户ID类型</li>
 *   <li>U: 用户实体类型</li>
 *   <li>P: 登录参数类型（不同登录方式的参数不同）</li>
 *   <li>R: 登录结果类型</li>
 * </ul>
 * <p>
 * <b>使用示例：</b>实现类定义mode枚举（如"username"、"sms"），在isSupport中判断
 *
 */
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
            throw new ServiceException(SecurityExceptionEnum.LOGIN_FAILED_USER_VERIFY_ERROR);
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
        throw new ServiceException(SecurityExceptionEnum.LOGIN_MODEL_UNSUPPORTED, mode);
    }
}
