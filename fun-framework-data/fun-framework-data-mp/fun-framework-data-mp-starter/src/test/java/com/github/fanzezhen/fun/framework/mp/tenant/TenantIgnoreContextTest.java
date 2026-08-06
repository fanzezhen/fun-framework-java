package com.github.fanzezhen.fun.framework.mp.tenant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 租户逃生口线程上下文测试
 *
 * @since 4.1.1
 */
@DisplayName("TenantIgnoreContext 租户逃生口上下文")
class TenantIgnoreContextTest {

    @AfterEach
    void tearDown() {
        TenantIgnoreContext.clear();
    }

    @Test
    @DisplayName("isIgnore_未设置_返回false")
    void isIgnore_NotSet_ShouldReturnFalse() {
        assertFalse(TenantIgnoreContext.isIgnore());
    }

    @Test
    @DisplayName("isIgnore_置位后_返回对应值")
    void isIgnore_AfterSet_ShouldReturnValue() {
        TenantIgnoreContext.set(true);
        boolean afterSetTrue = TenantIgnoreContext.isIgnore();
        TenantIgnoreContext.set(false);
        boolean afterSetFalse = TenantIgnoreContext.isIgnore();

        assertAll(
                () -> assertTrue(afterSetTrue),
                () -> assertFalse(afterSetFalse)
        );
    }

    @Test
    @DisplayName("clear_置位后_恢复未设置状态")
    void clear_AfterSet_ShouldResetToUnset() {
        TenantIgnoreContext.set(true);
        TenantIgnoreContext.clear();

        assertFalse(TenantIgnoreContext.isIgnore());
    }

    @Test
    @DisplayName("isIgnore_复用已有线程_看不到本线程置位")
    void isIgnore_ReusedThread_ShouldNotSeeCurrentThreadValue() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            // 先跑一个空任务让工作线程建好，排除 InheritableThreadLocal 的创建期继承
            executor.submit(() -> null).get();
            TenantIgnoreContext.set(true);
            Future<Boolean> future = executor.submit(TenantIgnoreContext::isIgnore);

            assertAll(
                    () -> assertTrue(TenantIgnoreContext.isIgnore()),
                    () -> assertFalse(future.get(), "未经 TTL 装饰的线程池复用时不应看到调用方的逃生状态")
            );
        } finally {
            executor.shutdownNow();
        }
    }
}
