package com.github.fanzezhen.fun.framework.trace.impl.model.bo;

import com.github.fanzezhen.fun.framework.core.model.bo.BaseBO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.ibatis.mapping.SqlCommandType;

/**
 * 痕迹表
 *
 * @author fanzezhen
 * @since 3.4.3.1
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class TraceBO extends BaseBO<String> {

    /**
     * 名称
     */
    protected String name;

    /**
     * 标识
     */
    protected String code;

    /**
     * 值
     */
    protected String value;

    /**
     * 操作类型
     */
    protected SqlCommandType type;

    /**
     * 上下文痕迹ID
     */
    protected String traceId;

    /**
     * 业务表ID
     */
    protected String businessId;
    /**
     * 创建人
     */
    protected String creator;

}
