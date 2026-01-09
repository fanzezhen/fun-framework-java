package com.github.fanzezhen.fun.framework.trace.impl.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 痕迹明细表
 *
 * @author fanzezhen
 * @since 3.4.3.1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
@TableName(value = "fun_trace_detail", autoResultMap = true)
public class TraceDetailEntity extends BaseEntity{

    /**
     * 业务表ID
     */
    private String businessId;

    /**
     * 痕迹表ID
     */
    private Long traceId;

    /**
     * 名称
     */
    private String name;

    /**
     * 标识
     */
    private String code;

    /**
     * 旧值
     */
    private String oldValue;

    /**
     * 新值
     */
    private String newValue;

    public TraceDetailEntity(Long id) {
        setId(id);
    }
}
