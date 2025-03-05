package com.github.fanzezhen.fun.framework.mp.trace.model.web.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 痕迹明细表
 *
 * @author fanzezhen
 * @since 3.4.3.1
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class TraceDetailResponse {

    /**
     * 业务表ID
     */
    private Long businessId;

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

    /**
     * 主键
     */
    private String id;

    /**
     * 删除标志位（0代表未删除，非0即为已删除）
     */
    private Integer deleteFlag;

    /**
     * 最后更新时间
     */
    private Date updateTime;

    /**
     * 最后更新人ID
     */
    private String updateBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人ID
     */
    private String createBy;
}
