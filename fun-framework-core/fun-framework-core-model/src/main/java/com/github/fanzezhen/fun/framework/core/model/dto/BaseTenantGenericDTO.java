package com.github.fanzezhen.fun.framework.core.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 公共业务租户常规模型类
 *
 * @author fanzezhen
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseTenantGenericDTO<P extends Serializable> extends BaseGenericDTO<P> {
    /**
     * 租户id
     */
    @Schema(name = "租户id")
    protected String tenantId;

    public BaseTenantGenericDTO<P> init(BaseTenantGenericDTO<P> dto) {
        super.init(dto);
        this.tenantId = dto.getTenantId();
        return this;
    }

}
