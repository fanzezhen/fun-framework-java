package com.github.fanzezhen.fun.framework.mp.tenant;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * {@link IgnoreTenant} 切面
 * <p>
 * 进入被标注的方法（或被标注类的任意方法）时开启忽略上下文，退出时<b>还原为进入前的值</b>而非
 * 一律清除——嵌套调用（跨租户方法 A 调用跨租户方法 B）中若内层直接清除，B 返回后 A 剩余逻辑
 * 会悄悄恢复租户过滤，同一次调用里出现两种隔离行为。
 * </p>
 *
 * @since 4.1.1
 */
@Aspect
public class TenantIgnoreAspect {

    /**
     * 环绕 {@link IgnoreTenant} 标注的方法或类，执行期间跨租户，退出（含异常）还原
     *
     * @param joinPoint 连接点
     * @return 原方法返回值
     * @throws Throwable 原方法抛出的异常原样透传
     */
    @Around("@annotation(com.github.fanzezhen.fun.framework.mp.tenant.IgnoreTenant)"
            + " || @within(com.github.fanzezhen.fun.framework.mp.tenant.IgnoreTenant)")
    public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {
        boolean previous = TenantIgnoreContext.isIgnore();
        try {
            TenantIgnoreContext.set(true);
            return joinPoint.proceed();
        } finally {
            if (previous) {
                TenantIgnoreContext.set(true);
            } else {
                TenantIgnoreContext.clear();
            }
        }
    }
}
