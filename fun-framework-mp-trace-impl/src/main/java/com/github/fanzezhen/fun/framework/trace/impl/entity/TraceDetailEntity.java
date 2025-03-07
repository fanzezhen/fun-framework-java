package com.github.fanzezhen.fun.framework.trace.impl.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.fanzezhen.fun.framework.mp.base.entity.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 痕迹明细表
 *
 * @author fanzezhen
 * @createTime 2025-01-13 17:12:18
 * @since 3.4.3.1
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@TableName(value = "fun_trace_detail", autoResultMap = true)
public class TraceDetailEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /**
     * 业务表ID
     */
    private String businessId;

    /**
     * 痕迹表ID
     */
    private String traceId;

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

    public TraceDetailEntity(String id) {
        setId(id);
    }

}
