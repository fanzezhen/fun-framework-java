package com.github.fanzezhen.fun.framework.core.model.common;

/**
 * 用户接口
 * <p>
 * 定义用户的基本信息和账户状态，包括登录标识、用户名、账户有效性等。
 * </p>
 *
 * @param <K> 登录标识类型
 */
public interface IUser<K> {

    /**
     * 获取登录标识
     * <p>
     * 建议返回用户ID或用户名作为登录唯一标识
     * </p>
     *
     * @return 登录标识
     */
    K getLoginCode();

    /**
     * 获取用户名
     * <p>
     * 返回用于认证用户身份的用户名，不能返回 null
     * </p>
     *
     * @return 用户名（永不为 null）
     */
    String getUsername();

    /**
     * 判断账户是否未过期
     * <p>
     * 过期的账户无法通过认证
     * </p>
     *
     * @return true 表示账户有效（未过期），false 表示账户已过期
     */
    default boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 判断账户是否未锁定
     * <p>
     * 锁定的账户无法通过认证
     * </p>
     *
     * @return true 表示账户未锁定，false 表示账户已锁定
     */
    default boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 判断凭证（密码）是否未过期
     * <p>
     * 过期的凭证无法通过认证
     * </p>
     *
     * @return true 表示凭证有效（未过期），false 表示凭证已过期
     */
    default boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 判断用户是否已启用
     * <p>
     * 禁用的用户无法通过认证
     * </p>
     *
     * @return true 表示用户已启用，false 表示用户已禁用
     */
    default boolean isEnabled() {
        return true;
    }
}
