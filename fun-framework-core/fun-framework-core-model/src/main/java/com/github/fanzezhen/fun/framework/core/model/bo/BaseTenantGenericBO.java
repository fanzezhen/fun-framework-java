package com.github.fanzezhen.fun.framework.core.model.bo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 公共业务租户常规模型类
 * <p>
 * 继承 BaseGenericBO，额外提供租户ID字段，适用于多租户场景下需要记录完整生命周期的业务对象。
 * </p>
 *
 * @param <P> 主键类型
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseTenantGenericBO<P extends Serializable> extends BaseGenericBO<P> {
    /**
     * 租户ID
     */
    protected P tenantId;

    /**
     * 从另一个 BaseTenantGenericBO 对象初始化当前对象的所有字段
     *
     * @param dto 源对象
     */
    public void init(BaseTenantGenericBO<P> dto) {
        super.init(dto);
        this.tenantId = dto.getTenantId();
    }

}
