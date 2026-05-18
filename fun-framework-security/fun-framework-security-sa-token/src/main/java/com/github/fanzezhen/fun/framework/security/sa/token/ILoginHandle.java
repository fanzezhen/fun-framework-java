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
 */
@SuppressWarnings({"unchecked", "unused"})
public interface ILoginHandle<
    K extends Serializable,
    U extends IUser<K>,
    P extends ILoginParameter,
    R extends ILoginResult<K, U>
    > extends InitializingBean {

    /**
     * 登录处理器全局列表。
     * <p>
     * 所有实现类在初始化时会自动注册到此列表中。
     */
    List<ILoginHandle<?, ?, ?, ?>> LOGIN_HANDLE_LIST = new ArrayList<>();

    /**
     * 判断是否支持指定的登录方式。
     *
     * @param mode 登录模式（如 "username"、"sms"、"oauth" 等）
     * @return true 表示支持，false 表示不支持
     */
    boolean isSupport(Object mode);

    /**
     * 验证登录用户身份。
     *
     * @param parameter 登录参数
     * @return 验证通过的用户对象，如果验证失败则返回 null
     */
    U verify(P parameter);

    /**
     * 制作登录结果对象。
     *
     * @param user      用户对象
     * @param tokenInfo Token 信息
     * @return 登录结果对象
     */
    R makeLoginResult(U user, SaTokenInfo tokenInfo);

    /**
     * 执行登录操作。
     * <p>
     * 默认实现：验证用户 → 调用 Sa-Token 登录 → 制作登录结果。
     *
     * @param parameter 登录参数
     * @return 登录结果
     * @throws ServiceException 如果用户验证失败
     */
    default R doLogin(final P parameter) {
        U user = verify(parameter);
        if (user == null) {
            throw new ServiceException(SecurityExceptionEnum.LOGIN_FAILED_USER_VERIFY_ERROR);
        }
        StpUtil.login(user.getLoginCode());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return makeLoginResult(user, tokenInfo);
    }

    /**
     * 执行退出登录操作。
     * <p>
     * 默认实现：调用 Sa-Token 注销当前登录。
     *
     * @return 注销结果，默认返回 null
     */
    default Object doLogout() {
        StpUtil.logout();
        return null;
    }

    /**
     * Spring Bean 初始化后的回调方法。
     * <p>
     * 自动将当前处理器注册到全局列表中。
     */
    @Override
    default void afterPropertiesSet() {
        LOGIN_HANDLE_LIST.add(this);
    }

    /**
     * 根据登录模式获取对应的登录处理器。
     *
     * @param <T>  登录处理器类型
     * @param mode 登录模式
     * @return 匹配的登录处理器
     * @throws ServiceException 如果没有找到支持该模式的处理器
     */
    static <T extends ILoginHandle<?, ?, ?, ?>> T getLoginHandle(final Object mode) {
        for (ILoginHandle<?, ?, ?, ?> loginHandle : LOGIN_HANDLE_LIST) {
            if (loginHandle.isSupport(mode)) {
                return (T) loginHandle;
            }
        }
        throw new ServiceException(SecurityExceptionEnum.LOGIN_MODEL_UNSUPPORTED, mode);
    }
}
