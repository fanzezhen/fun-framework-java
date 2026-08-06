package com.github.fanzezhen.fun.framework.mp.tenant;

import com.github.fanzezhen.fun.framework.core.context.ContextHolder;
import com.github.fanzezhen.fun.framework.mp.config.FunMpProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 租户上下文拦截器骨架测试
 *
 * @since 4.1.1
 */
@DisplayName("AbstractTenantContextInterceptor 租户上下文拦截器")
class AbstractTenantContextInterceptorTest {

    @AfterEach
    void tearDown() {
        ContextHolder.clean();
    }

    /**
     * 造一份指定默认租户的配置
     *
     * @param defaultTenantId 默认租户 ID
     * @return MyBatis-Plus 增强配置
     */
    private FunMpProperties properties(final String defaultTenantId) {
        FunMpProperties properties = new FunMpProperties();
        properties.getTenant().setDefaultTenantId(defaultTenantId);
        return properties;
    }

    @Test
    @DisplayName("preHandle_解析到租户_写入上下文")
    void preHandle_TenantResolved_ShouldSetContext() {
        AbstractTenantContextInterceptor interceptor = new FixedTenantInterceptor(properties("0"), "1001");
        MockHttpServletRequest request = new MockHttpServletRequest();

        boolean proceed = interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        assertAll(
                () -> assertTrue(proceed, "本拦截器不应拦断请求"),
                () -> assertEquals("1001", ContextHolder.getTenantId())
        );
    }

    @Test
    @DisplayName("preHandle_未解析到租户_兜底默认租户")
    void preHandle_TenantMissing_ShouldFallbackToDefault() {
        AbstractTenantContextInterceptor interceptor = new FixedTenantInterceptor(properties("9"), null);
        MockHttpServletRequest request = new MockHttpServletRequest();

        interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        assertEquals("9", ContextHolder.getTenantId());
    }

    @Test
    @DisplayName("preHandle_解析到空白串_兜底默认租户")
    void preHandle_BlankTenant_ShouldFallbackToDefault() {
        AbstractTenantContextInterceptor interceptor = new FixedTenantInterceptor(properties("9"), "  ");
        MockHttpServletRequest request = new MockHttpServletRequest();

        interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        assertEquals("9", ContextHolder.getTenantId());
    }

    @Test
    @DisplayName("preHandle_关闭兜底且无租户_不写入上下文")
    void preHandle_FallbackDisabled_ShouldLeaveContextEmpty() {
        AbstractTenantContextInterceptor interceptor = new NoFallbackInterceptor();
        MockHttpServletRequest request = new MockHttpServletRequest();

        interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        assertNull(ContextHolder.getTenantId(), "覆写 getDefaultTenantId 返回 null 即把处置权交回 missing-strategy");
    }

    @Test
    @DisplayName("afterCompletion_本拦截器写入过_清除租户键")
    void afterCompletion_AppliedByThis_ShouldClearTenantId() {
        AbstractTenantContextInterceptor interceptor = new FixedTenantInterceptor(properties("0"), "1001");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        interceptor.preHandle(request, response, new Object());

        interceptor.afterCompletion(request, response, new Object(), null);

        assertNull(ContextHolder.getTenantId());
    }

    @Test
    @DisplayName("afterCompletion_本拦截器未写入_不动外层租户号")
    void afterCompletion_NotAppliedByThis_ShouldKeepOuterTenantId() {
        AbstractTenantContextInterceptor interceptor = new NoFallbackInterceptor();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        // 模拟外层过滤器已写入租户号，而本拦截器解析不到、也不兜底
        ContextHolder.setTenantId("outer");
        interceptor.preHandle(request, response, new Object());

        interceptor.afterCompletion(request, response, new Object(), null);

        assertEquals("outer", ContextHolder.getTenantId(), "不应清掉非本拦截器写入的租户号");
    }

    @Test
    @DisplayName("afterCompletion_请求处理异常_仍清除租户键")
    void afterCompletion_WithException_ShouldStillClear() {
        AbstractTenantContextInterceptor interceptor = new FixedTenantInterceptor(properties("0"), "1001");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        interceptor.preHandle(request, response, new Object());

        interceptor.afterCompletion(request, response, new Object(), new IllegalStateException("测试异常"));

        assertNull(ContextHolder.getTenantId());
    }

    @Test
    @DisplayName("getDefaultTenantId_无配置构造_取内置缺省值")
    void getDefaultTenantId_WithoutProperties_ShouldUseConstant() {
        AbstractTenantContextInterceptor interceptor = new FixedTenantInterceptor(null, null);
        MockHttpServletRequest request = new MockHttpServletRequest();

        interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        assertEquals(FunTenantConstant.DEFAULT_TENANT_ID, ContextHolder.getTenantId());
    }

    /**
     * 返回固定租户号的测试拦截器
     */
    static class FixedTenantInterceptor extends AbstractTenantContextInterceptor {

        /**
         * 固定返回的租户号
         */
        private final String tenantId;

        FixedTenantInterceptor(final FunMpProperties properties, final String tenantId) {
            super(properties);
            this.tenantId = tenantId;
        }

        @Override
        protected String resolveTenantId(final HttpServletRequest request) {
            return tenantId;
        }
    }

    /**
     * 关闭默认租户兜底的测试拦截器
     */
    static class NoFallbackInterceptor extends AbstractTenantContextInterceptor {

        @Override
        protected String resolveTenantId(final HttpServletRequest request) {
            return null;
        }

        @Override
        protected String getDefaultTenantId() {
            return null;
        }
    }
}
