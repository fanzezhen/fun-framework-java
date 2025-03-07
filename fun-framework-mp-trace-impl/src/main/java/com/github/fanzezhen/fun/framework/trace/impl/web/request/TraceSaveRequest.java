package com.github.fanzezhen.fun.framework.trace.impl.web.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 痕迹表
 *
 * @author fanzezhen
 * @createTime 2025-01-13 17:12:18
 * @since 3.4.3.1
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class TraceSaveRequest implements Serializable {
    /**
     * 主键
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 标识
     */
    private String code;

    /**
     * 值
     */
    private String value;

    /**
     * 操作类型
     */
    private String type;

    /**
     * 上下文痕迹ID
     */
    private String traceId;

    /**
     * 业务表ID
     */
    private String businessId;
}
