package com.github.fanzezhen.fun.framework.core.model;

/**
 * 用户
 */
public interface IUser<K> {

    /**
     * 登录标识，建议返回id或username
     */
    K getLoginCode();

    /**
     * Returns the username used to authenticate the user. Cannot return
     * <code>null</code>.
     * @return the username (never <code>null</code>)
     */
    String getUsername();

    /**
     * Indicates whether the user's account has expired. An expired account cannot be
     * authenticated.
     * @return <code>true</code> if the user's account is valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    default boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
     */
    default boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     * @return <code>true</code> if the user's credentials are valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    default boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
     */
    default boolean isEnabled() {
        return true;
    }
}
