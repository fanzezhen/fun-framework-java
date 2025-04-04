package com.github.fanzezhen.fun.framework.trace.impl.web.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 痕迹明细表
 *
 * @author fanzezhen
 * @since 3.4.3.1
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class TraceDetailSaveRequest implements Serializable {
    /**
     * 主键
     */
    private String id;

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
}
