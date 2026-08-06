package com.github.fanzezhen.fun.framework.mp.tenant;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.context.ContextHolder;
import com.github.fanzezhen.fun.framework.mp.config.FunMpProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 租户上下文请求拦截器骨架
 * <p>
 * 请求进入时把当前租户写入 {@code ContextHolder}，SQL 层的租户行处理器据此自动拼接隔离条件；
 * 请求结束（含异常）在 {@code afterCompletion} 中清除。业务侧只需实现
 * {@link #resolveTenantId(HttpServletRequest)}，从自己的登录态（Sa-Token、Spring Security、
 * 自研 JWT 皆可）取出租户号——框架不预设认证方案，故此处必须留给业务。
 * </p>
 * <p>
 * <b>只清租户键，不清整个上下文</b>：请求可能由外层过滤器（如 {@code FunContextFilter}）
 * 建立了 traceId 等上下文，本拦截器 {@code clean()} 会连带清掉它们。
 * </p>
 * <p>
 * <b>登录路径须在注册处排除</b>：默认实现在取不到租户号时兜底为
 * {@code fun.mp.tenant.default-tenant-id}，这使 web 请求永不触发 SQL 层的
 * {@code missing-strategy} 判定。登录本身要跨租户按账号查用户，应走 {@code @IgnoreTenant}，
 * 并在 {@code addInterceptors} 时用 {@code excludePathPatterns} 排除登录路径。不需要兜底时
 * 覆写 {@link #getDefaultTenantId()} 返回 {@code null}，交由 {@code missing-strategy} 处置。
 * </p>
 * <p>使用示例：</p>
 * <pre>{@code
 * public class MyTenantInterceptor extends AbstractTenantContextInterceptor {
 *     public MyTenantInterceptor(FunMpProperties properties) {
 *         super(properties);
 *     }
 *
 *     @Override
 *     protected String resolveTenantId(HttpServletRequest request) {
 *         return StpUtil.isLogin() ? StpUtil.getSession().getString("tenantId") : null;
 *     }
 * }
 * }</pre>
 *
 * @since 4.1.1
 */
public abstract class AbstractTenantContextInterceptor implements HandlerInterceptor {

    /**
     * 标记「本拦截器写入过租户上下文」的请求属性名
     * <p>
     * 只有自己写入过才在请求结束时清除，避免误清外层组件写入的租户号。
     * </p>
     */
    private static final String ATTRIBUTE_APPLIED = AbstractTenantContextInterceptor.class.getName() + ".APPLIED";

    /**
     * MyBatis-Plus 增强配置，允许为 {@code null}
     */
    private final FunMpProperties funMpProperties;

    /**
     * 构造拦截器，不提供配置则默认租户兜底取 {@link FunTenantConstant#DEFAULT_TENANT_ID}
     */
    protected AbstractTenantContextInterceptor() {
        this(null);
    }

    /**
     * 构造拦截器
     *
     * @param funMpProperties MyBatis-Plus 增强配置，用于读取默认租户 ID，允许为 {@code null}
     */
    protected AbstractTenantContextInterceptor(final FunMpProperties funMpProperties) {
        this.funMpProperties = funMpProperties;
    }

    /**
     * 从请求中解析当前租户 ID
     * <p>
     * 由业务侧从登录态取值。返回空值表示「无法确定租户」，此时走
     * {@link #getDefaultTenantId()} 兜底。
     * </p>
     *
     * @param request 当前请求
     * @return 租户 ID，无法确定时返回 {@code null}
     */
    protected abstract String resolveTenantId(HttpServletRequest request);

    /**
     * 取兜底的默认租户 ID
     * <p>
     * 默认返回配置项 {@code fun.mp.tenant.default-tenant-id}。覆写返回 {@code null} 即关闭兜底，
     * 把「无租户上下文」的处置权交回 SQL 层的 {@code missing-strategy}。
     * </p>
     *
     * @return 默认租户 ID，{@code null} 表示不兜底
     */
    protected String getDefaultTenantId() {
        if (funMpProperties == null) {
            return FunTenantConstant.DEFAULT_TENANT_ID;
        }
        return funMpProperties.getTenant().getDefaultTenantId();
    }

    /**
     * 前置处理：解析并写入租户上下文
     *
     * @param request  当前请求
     * @param response 当前响应
     * @param handler  处理器
     * @return 恒为 {@code true}，本拦截器不拦断请求
     */
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
                             final Object handler) {
        String tenantId = resolveTenantId(request);
        if (CharSequenceUtil.isBlank(tenantId)) {
            tenantId = getDefaultTenantId();
        }
        if (CharSequenceUtil.isNotBlank(tenantId)) {
            ContextHolder.setTenantId(tenantId);
            request.setAttribute(ATTRIBUTE_APPLIED, Boolean.TRUE);
        }
        return true;
    }

    /**
     * 请求结束（含异常）清除本拦截器写入的租户上下文，防线程池复用导致的越权
     *
     * @param request  当前请求
     * @param response 当前响应
     * @param handler  处理器
     * @param ex       处理过程中抛出的异常，无异常时为 {@code null}
     */
    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
                                final Object handler, final Exception ex) {
        if (Boolean.TRUE.equals(request.getAttribute(ATTRIBUTE_APPLIED))) {
            ContextHolder.clearTenantId();
            request.removeAttribute(ATTRIBUTE_APPLIED);
        }
    }
}
