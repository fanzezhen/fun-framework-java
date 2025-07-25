package com.github.fanzezhen.fun.framework.core.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公共业务常规模型类
 *
 * @author fanzezhen
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseGenericBO<P extends Serializable> extends BaseBO<P> {

    /**
     * 更新时间
     */
    @Schema(name = "更新时间")
    protected LocalDateTime updateTime;

    /**
     * 更新者ID
     */
    @Schema(name = "更新者ID")
    protected P updateUserId;

    public void init(BaseGenericBO<P> dto) {
        super.init(dto);
        this.updateTime = dto.getUpdateTime();
        this.updateUserId = dto.getUpdateUserId();
    }

}
