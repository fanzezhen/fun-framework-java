package com.github.fanzezhen.fun.framework.mp.tenant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 租户逃生口切面测试
 *
 * @since 4.1.1
 */
@DisplayName("TenantIgnoreAspect 租户逃生口切面")
class TenantIgnoreAspectTest {

    @AfterEach
    void tearDown() {
        TenantIgnoreContext.clear();
    }

    /**
     * 织入切面，返回代理对象
     *
     * @param target 目标对象
     * @param <T>    目标类型
     * @return 代理对象
     */
    private <T> T proxy(final T target) {
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(new TenantIgnoreAspect());
        return factory.getProxy();
    }

    @Test
    @DisplayName("around_方法级注解_执行期开启退出清理")
    void around_MethodAnnotated_ShouldIgnoreDuringAndClearAfter() {
        MethodAnnotatedService service = proxy(new MethodAnnotatedService());

        boolean during = service.crossTenant();

        assertAll(
                () -> assertTrue(during, "标注方法执行期间应处于跨租户状态"),
                () -> assertFalse(TenantIgnoreContext.isIgnore(), "方法退出后应清理")
        );
    }

    @Test
    @DisplayName("around_未标注方法_不开启跨租户")
    void around_PlainMethod_ShouldNotIgnore() {
        MethodAnnotatedService service = proxy(new MethodAnnotatedService());

        assertFalse(service.normal());
    }

    @Test
    @DisplayName("around_类级注解_类内方法均生效")
    void around_TypeAnnotated_ShouldIgnoreForAllMethods() {
        TypeAnnotatedService service = proxy(new TypeAnnotatedService());

        assertAll(
                () -> assertTrue(service.first()),
                () -> assertTrue(service.second()),
                () -> assertFalse(TenantIgnoreContext.isIgnore())
        );
    }

    @Test
    @DisplayName("around_方法抛异常_仍清理上下文")
    void around_MethodThrows_ShouldStillClear() {
        MethodAnnotatedService service = proxy(new MethodAnnotatedService());

        assertAll(
                () -> assertThrows(IllegalStateException.class, service::crossTenantThenThrow),
                () -> assertFalse(TenantIgnoreContext.isIgnore(), "异常退出也必须清理，否则线程池复用会越权")
        );
    }

    @Test
    @DisplayName("around_嵌套调用_内层退出不影响外层")
    void around_NestedCall_ShouldRestoreOuterState() {
        NestedService target = new NestedService();
        NestedService service = proxy(target);
        // 内层需经代理调用才会再次触发切面，模拟嵌套的两个跨租户方法
        target.setSelf(service);

        assertAll(
                () -> assertEquals("true/true", service.outer(), "内层返回后外层仍应处于跨租户状态"),
                () -> assertFalse(TenantIgnoreContext.isIgnore())
        );
    }

    /**
     * 方法级标注的测试服务
     */
    public static class MethodAnnotatedService {

        /**
         * 跨租户方法
         *
         * @return 执行期间的逃生状态
         */
        @IgnoreTenant
        public boolean crossTenant() {
            return TenantIgnoreContext.isIgnore();
        }

        /**
         * 跨租户后抛异常的方法
         */
        @IgnoreTenant
        public void crossTenantThenThrow() {
            throw new IllegalStateException("测试异常路径");
        }

        /**
         * 未标注的普通方法
         *
         * @return 执行期间的逃生状态
         */
        public boolean normal() {
            return TenantIgnoreContext.isIgnore();
        }
    }

    /**
     * 类级标注的测试服务
     */
    @IgnoreTenant
    public static class TypeAnnotatedService {

        /**
         * 类内方法一
         *
         * @return 执行期间的逃生状态
         */
        public boolean first() {
            return TenantIgnoreContext.isIgnore();
        }

        /**
         * 类内方法二
         *
         * @return 执行期间的逃生状态
         */
        public boolean second() {
            return TenantIgnoreContext.isIgnore();
        }
    }

    /**
     * 嵌套调用的测试服务
     */
    public static class NestedService {

        /**
         * 自身代理，用于让内层调用也走切面
         */
        private NestedService self;

        /**
         * 注入自身代理
         *
         * @param self 自身代理
         */
        public void setSelf(final NestedService self) {
            this.self = self;
        }

        /**
         * 外层跨租户方法
         *
         * @return 「内层状态/内层返回后的外层状态」
         */
        @IgnoreTenant
        public String outer() {
            boolean inner = self == null ? TenantIgnoreContext.isIgnore() : self.inner();
            return inner + "/" + TenantIgnoreContext.isIgnore();
        }

        /**
         * 内层跨租户方法
         *
         * @return 执行期间的逃生状态
         */
        @IgnoreTenant
        public boolean inner() {
            return TenantIgnoreContext.isIgnore();
        }
    }
}
