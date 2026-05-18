package com.github.fanzezhen.fun.framework.mp.base.entity.increment.tenant;

import cn.hutool.core.util.ArrayUtil;
import com.github.fanzezhen.fun.framework.mp.base.entity.increment.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 自增主键多租户实体基类
 * <p>
 * 在 {@link BaseEntity} 基础上扩展多租户支持，适用于SaaS系统中的数据隔离场景。
 * 主键类型为 Integer，包含租户ID字段：
 * <ul>
 *   <li>tenantId - 租户ID（Integer类型，与主键类型一致）</li>
 * </ul>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>SaaS多租户系统</li>
 *   <li>需要租户级数据隔离</li>
 *   <li>支持MyBatis-Plus多租户插件</li>
 * </ul>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseTenantEntity extends BaseEntity {

    /**
     * 租户ID
     * <p>
     * 用于多租户数据隔离，配合MyBatis-Plus多租户插件使用。
     * 类型与主键类型一致（Integer）。
     */
    protected Integer tenantId;

    /**
     * 获取所有字段的数据库列名数组（包含父类字段）
     *
     * @return 字段名数组
     */
    public static String[] getFieldNames() {
        return ArrayUtil.append(BaseEntity.getFieldNames(), "TENANT_ID");
    }
}
