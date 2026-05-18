package com.github.fanzezhen.fun.framework.trace.impl.model.bo;

import com.github.fanzezhen.fun.framework.core.model.bo.BaseBO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据追踪明细业务对象
 * <p>
 * 用于 Service 层和 Controller 层之间传递追踪明细数据。
 * 对应数据库表：fun_trace_detail
 *
 * @since 3.4.3.1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class TraceDetailBO extends BaseBO<String> {

    /**
     * 业务主键ID
     */
    private String businessId;

    /**
     * 追踪主表ID
     */
    private String traceId;

    /**
     * 字段显示名称
     */
    private String name;

    /**
     * 字段标识（数据库列名）
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
