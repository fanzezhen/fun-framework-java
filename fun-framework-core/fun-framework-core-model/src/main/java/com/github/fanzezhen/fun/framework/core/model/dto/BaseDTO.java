package com.github.fanzezhen.fun.framework.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公共数据传输对象基类
 * <p>
 * 提供数据传输对象(Data Transfer Object)的基础属性，包括主键、创建时间和创建人ID。
 * 所有 DTO 应继承此类以获得统一的基础字段定义。
 * </p>
 *
 * @param <P> 主键类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseDTO<P extends Serializable> implements Serializable {
    /**
     * 主键
     */
    private P id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    private P createUserId;

    /**
     * 从另一个 BaseDTO 对象初始化当前对象的基础字段
     *
     * @param baseVarEntry 源对象
     */
    public void init(BaseDTO<P> baseVarEntry) {
        this.id = baseVarEntry.getId();
        this.createTime = baseVarEntry.getCreateTime();
        this.createUserId = baseVarEntry.getCreateUserId();
    }

}
