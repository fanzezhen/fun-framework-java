package com.github.fanzezhen.fun.framework.core.model.bo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 公共业务租户模型类
 * <p>
 * 继承 BaseBO，额外提供租户ID字段，适用于多租户场景下的业务对象。
 * </p>
 *
 * @param <P> 主键类型
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseTenantBO<P extends Serializable> extends BaseBO<P> {
    /**
     * 租户ID
     */
    protected P tenantId;

    /**
     * 从另一个 BaseTenantBO 对象初始化当前对象的所有字段
     *
     * @param dto 源对象
     */
    public void init(BaseTenantBO<P> dto) {
        super.init(dto);
        this.tenantId = dto.getTenantId();
    }

}
