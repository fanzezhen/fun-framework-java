package com.github.fanzezhen.fun.framework.mp.tenant;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 租户隔离「逃生口」的线程上下文开关
 * <p>
 * 值为 {@code true} 时，租户行处理器跳过租户条件拼接，用于登录查账号、平台管理员跨租户操作、
 * 定时任务等需要跨租户访问的场景。通常由 {@link TenantIgnoreAspect} 在方法进入时置位、
 * 退出时还原，业务代码一般不直接调用。
 * </p>
 * <p>
 * 载体为 {@link TransmittableThreadLocal}，与 {@code ContextHolder} 一致，配合 TTL 装饰的线程池
 * 可跨线程传递。手工调用 {@link #set(boolean)} 时<b>务必</b>在 {@code finally} 中
 * {@link #clear()}，否则线程池复用会把跨租户状态泄漏给后续无关请求——那是越权读写。
 * </p>
 *
 * @since 4.1.1
 */
public final class TenantIgnoreContext {

    /**
     * 忽略标志的线程上下文
     */
    private static final TransmittableThreadLocal<Boolean> IGNORE = new TransmittableThreadLocal<>();

    private TenantIgnoreContext() {
    }

    /**
     * 设置是否忽略租户隔离
     *
     * @param ignore {@code true}=跳过租户条件拼接
     */
    public static void set(final boolean ignore) {
        IGNORE.set(ignore);
    }

    /**
     * 当前线程是否处于忽略租户隔离状态
     *
     * @return {@code true}=跳过租户条件；未设置时返回 {@code false}
     */
    public static boolean isIgnore() {
        return Boolean.TRUE.equals(IGNORE.get());
    }

    /**
     * 清除忽略标志，方法退出务必调用
     */
    public static void clear() {
        IGNORE.remove();
    }
}
