package com.github.fanzezhen.fun.framework.mp.tenant;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.github.fanzezhen.fun.framework.core.context.ContextHolder;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.mp.config.FunMpProperties;
import com.github.fanzezhen.fun.framework.mp.enums.FunDataMpExceptionEnum;
import com.github.fanzezhen.fun.framework.mp.tenant.enums.TenantMissingStrategyEnum;
import com.github.fanzezhen.fun.framework.mp.tenant.enums.TenantValueTypeEnum;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;

import java.util.Set;

/**
 * 默认租户行处理器，三级判定隔离范围
 * <p>判定优先级（命中即返回）：</p>
 * <ol>
 *   <li><b>{@code @IgnoreTenant} 方法级逃生口</b>——{@link TenantIgnoreContext#isIgnore()}
 *   为真则整块跳过；</li>
 *   <li><b>{@code ignore-tenant-tables} 配置的永久例外表</b>——有租户列但需跨租户访问
 *   （如租户授权中间表）；</li>
 *   <li><b>列结构缓存（主判据）</b>——表含租户列才隔离，否则放行，避免对全局表误拼条件。
 *   缓存不可用时退化为「全表隔离」。</li>
 * </ol>
 * <p>
 * {@link #getTenantId()} 从 {@code ContextHolder} 取当前租户；无上下文时按
 * {@code fun.mp.tenant.missing-strategy} 处置——{@code default} 回退
 * {@code fun.mp.tenant.default-tenant-id}，{@code reject} 抛 {@link ServiceException} fail fast。
 * </p>
 *
 * @since 4.1.1
 */
public class DefaultTenantLineHandler implements TenantLineHandler {

    /**
     * 含租户列的表名缓存
     */
    private final TenantColumnCache columnCache;

    /**
     * 多租户配置
     */
    private final FunMpProperties.Tenant tenant;

    /**
     * 构造行处理器
     *
     * @param columnCache 含租户列的表名缓存
     * @param tenant      多租户配置
     */
    public DefaultTenantLineHandler(final TenantColumnCache columnCache, final FunMpProperties.Tenant tenant) {
        this.columnCache = columnCache;
        this.tenant = tenant;
    }

    /**
     * 取当前租户 ID 表达式
     * <p>
     * 返回值会被直接序列化进 SQL，故必须是字面量表达式而非参数占位。
     * </p>
     *
     * @return 租户 ID 字面量表达式
     * @throws ServiceException {@code missing-strategy=reject} 且上下文缺失，
     *                          或 {@code value-type=long} 但租户号非数值时抛出
     */
    @Override
    public Expression getTenantId() {
        String tenantId = ContextHolder.getTenantId();
        if (CharSequenceUtil.isBlank(tenantId)) {
            if (tenant.getMissingStrategy() == TenantMissingStrategyEnum.REJECT) {
                throw new ServiceException(FunDataMpExceptionEnum.TENANT_CONTEXT_MISSING);
            }
            tenantId = tenant.getDefaultTenantId();
        }
        return toExpression(tenantId);
    }

    /**
     * 按配置的值类型构造字面量
     *
     * @param tenantId 租户 ID
     * @return 字面量表达式
     * @throws ServiceException 值类型为 {@code long} 但租户号无法解析为数值
     */
    private Expression toExpression(final String tenantId) {
        if (tenant.getValueType() != TenantValueTypeEnum.LONG) {
            return new StringValue(tenantId);
        }
        try {
            return new LongValue(Long.parseLong(CharSequenceUtil.trim(tenantId)));
        } catch (NumberFormatException e) {
            throw new ServiceException(e, FunDataMpExceptionEnum.TENANT_ID_NOT_NUMERIC, tenantId);
        }
    }

    /**
     * 判断表是否跳过租户条件拼接
     *
     * @param tableName 表名
     * @return {@code true}=跳过
     */
    @Override
    public boolean ignoreTable(final String tableName) {
        if (TenantIgnoreContext.isIgnore()) {
            return true;
        }
        if (isConfiguredIgnore(tableName)) {
            return true;
        }
        if (!columnCache.available()) {
            return false;
        }
        return !columnCache.contains(tableName);
    }

    /**
     * 表是否在配置的例外清单中（大小写不敏感）
     *
     * @param tableName 表名
     * @return {@code true}=已配置为例外表
     */
    private boolean isConfiguredIgnore(final String tableName) {
        Set<String> ignoreTables = tenant.getIgnoreTenantTables();
        if (ignoreTables == null || ignoreTables.isEmpty()) {
            return false;
        }
        for (String ignoreTable : ignoreTables) {
            if (CharSequenceUtil.equalsIgnoreCase(tableName, ignoreTable)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 取租户列名
     *
     * @return 租户列名，由 {@code fun.mp.tenant.column} 配置
     */
    @Override
    public String getTenantIdColumn() {
        return tenant.getColumn();
    }
}
