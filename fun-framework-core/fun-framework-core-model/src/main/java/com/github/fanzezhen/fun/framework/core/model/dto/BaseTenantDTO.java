package com.github.fanzezhen.fun.framework.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 租户数据传输对象基类
 * <p>
 * 继承 BaseDTO，额外提供租户ID字段，适用于多租户场景下的数据传输对象。
 * </p>
 *
 * @param <P> 主键类型
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseTenantDTO<P extends Serializable> extends BaseDTO<P> {
    /**
     * 租户ID
     */
    protected P tenantId;

    /**
     * 从另一个 BaseTenantDTO 对象初始化当前对象的所有字段
     *
     * @param dto 源对象
     */
    public void init(BaseTenantDTO<P> dto) {
        super.init(dto);
        this.tenantId = dto.getTenantId();
    }

}
