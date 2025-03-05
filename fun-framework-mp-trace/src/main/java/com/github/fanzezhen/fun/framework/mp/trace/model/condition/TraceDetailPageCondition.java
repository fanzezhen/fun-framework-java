package com.github.fanzezhen.fun.framework.mp.trace.model.condition;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.fanzezhen.fun.framework.mp.trace.model.entity.TraceDetailDO;
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
public class TraceDetailPageCondition extends PageDTO<TraceDetailDO> implements Serializable {

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
    
}
