package com.github.fanzezhen.fun.framework.mp.base.entity.uuid.tenant;

import cn.hutool.core.util.ArrayUtil;
import com.github.fanzezhen.fun.framework.mp.base.entity.uuid.BaseEntity;
import com.github.fanzezhen.fun.framework.mp.base.entity.uuid.BaseGenericEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据库租户常规实体类
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseTenantGenericEntity extends BaseGenericEntity {

    /**
     * 租户id
     */
    protected String tenantId;

    public static String[] getFieldNames() {
        return ArrayUtil.append(BaseEntity.getFieldNames(), "TENANT_ID");
    }
}
