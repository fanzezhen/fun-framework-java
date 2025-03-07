package com.github.fanzezhen.fun.framework.trace.impl.web.request;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.fanzezhen.fun.framework.trace.impl.entity.TraceEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 痕迹表
 *
 * @author fanzezhen
 * @since 3.4.3.1
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class TracePageRequest extends PageDTO<TraceEntity> {
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
    private String createBy;

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
