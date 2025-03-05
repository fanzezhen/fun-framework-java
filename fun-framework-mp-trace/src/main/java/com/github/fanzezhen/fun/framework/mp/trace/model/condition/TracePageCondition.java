package com.github.fanzezhen.fun.framework.mp.trace.model.condition;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.fanzezhen.fun.framework.mp.trace.model.entity.TraceDO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

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
public class TracePageCondition extends PageDTO<TraceDO> implements Serializable {
    /**
     * 关键词（模糊匹配：名称、标识、值）
     */
    private String keyword;
    /**
     * 开始时间
     */
    private Date beginTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 人员
     */
    private Long createBy;

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
    private Long businessId;
    
}
