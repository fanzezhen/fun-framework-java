package com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.tenant;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.BaseEntity;
import com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.BaseGenericEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据库租户常规实体类
 *
 * @author fanzezhen
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
    @Schema(name = "租户id")
    @TableField(value = "TENANT_ID")
    protected Long tenantId;

    public static String[] getFieldNames() {
        return ArrayUtil.append(BaseEntity.getFieldNames(), "TENANT_ID");
    }
}
