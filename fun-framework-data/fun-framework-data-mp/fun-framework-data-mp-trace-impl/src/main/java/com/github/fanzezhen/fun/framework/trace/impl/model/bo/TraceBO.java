package com.github.fanzezhen.fun.framework.trace.impl.model.bo;

import com.github.fanzezhen.fun.framework.core.model.bo.BaseBO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.ibatis.mapping.SqlCommandType;

/**
 * 数据追踪业务对象
 * <p>
 * 用于 Service 层和 Controller 层之间传递追踪主记录数据。
 * 对应数据库表：fun_trace
 *
 * @since 3.4.3.1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class TraceBO extends BaseBO<String> {

    /**
     * 业务对象名称
     */
    protected String name;

    /**
     * 追踪标识（通常为表名）
     */
    protected String code;

    /**
     * 业务对象标识值
     */
    protected String value;

    /**
     * SQL 操作类型
     */
    protected SqlCommandType type;

    /**
     * 上下文追踪ID
     */
    protected String traceId;

    /**
     * 业务主键ID
     */
    protected String businessId;

    /**
     * 创建人
     */
    protected String creator;

}
