package com.github.fanzezhen.fun.framework.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通用数据传输对象基类
 * <p>
 * 继承 BaseDTO，额外提供更新时间和更新人ID字段，适用于需要记录完整生命周期的数据传输对象。
 * </p>
 *
 * @param <P> 主键类型
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseGenericDTO<P extends Serializable> extends BaseDTO<P> {

    /**
     * 更新时间
     */
    protected LocalDateTime updateTime;

    /**
     * 更新者ID
     */
    protected P updateUserId;

    /**
     * 从另一个 BaseGenericDTO 对象初始化当前对象的所有字段
     *
     * @param dto 源对象
     */
    public void init(BaseGenericDTO<P> dto) {
        super.init(dto);
        this.updateTime = dto.getUpdateTime();
        this.updateUserId = dto.getUpdateUserId();
    }

}
