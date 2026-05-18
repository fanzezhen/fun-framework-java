package com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.tenant;

import cn.hutool.core.util.ArrayUtil;
import com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.BaseEntity;
import com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.BaseGenericEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 雪花算法多租户泛型实体基类
 * <p>
 * 在 {@link BaseGenericEntity} 基础上扩展多租户支持，适用于需要软删除、完整审计和租户隔离的场景。
 * 主键类型为 Long，包含租户ID字段：
 * <ul>
 *   <li>tenantId - 租户ID（Long类型，与主键类型一致）</li>
 * </ul>
 * <p>
 * 继承的字段包括：
 * <ul>
 *   <li>id、createTime、createUserId - 来自 {@link BaseEntity}</li>
 *   <li>delFlag、updateTime、updateUserId - 来自 {@link BaseGenericEntity}</li>
 * </ul>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseTenantGenericEntity extends BaseGenericEntity {

    /**
     * 租户ID
     * <p>
     * 用于多租户数据隔离，配合MyBatis-Plus多租户插件使用。
     * 类型与主键类型一致（Long）。
     */
    protected Long tenantId;

    /**
     * 获取所有字段的数据库列名数组（包含父类字段）
     *
     * @return 字段名数组
     */
    public static String[] getFieldNames() {
        return ArrayUtil.append(BaseEntity.getFieldNames(), "TENANT_ID");
    }
}
