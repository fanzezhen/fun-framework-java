package com.github.fanzezhen.fun.framework.trace.impl.web.request;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.fanzezhen.fun.framework.trace.impl.entity.TraceDetailEntity;
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
public class TraceDetailPageRequest extends PageDTO<TraceDetailEntity> {

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
