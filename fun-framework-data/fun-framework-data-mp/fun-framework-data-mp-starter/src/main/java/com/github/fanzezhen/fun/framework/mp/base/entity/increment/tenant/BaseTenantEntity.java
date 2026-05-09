package com.github.fanzezhen.fun.framework.mp.base.entity.increment.tenant;

import cn.hutool.core.util.ArrayUtil;
import com.github.fanzezhen.fun.framework.mp.base.entity.increment.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据库租户实体类
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseTenantEntity extends BaseEntity {

    /**
     * 租户id
     */
    protected Integer tenantId;

    public static String[] getFieldNames() {
        return ArrayUtil.append(BaseEntity.getFieldNames(), "TENANT_ID");
    }
}
