package com.github.fanzezhen.fun.framework.mp.trace.model.web.response;

import com.github.fanzezhen.fun.framework.mp.trace.model.bo.TraceDetailBO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 痕迹表
 *
 * @author fanzezhen
 * @since 3.4.3.1
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class TraceResponse {
    /**
     * 详情列表
     */
    private List<TraceDetailBO> detailList;
    /**
     * 变更内容
     */
    private String details;
    /**
     * 变更类型
     */
    private String typeName;

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
    /**
     * 创建人
     */
    private String creator;

    /**
     * 主键
     */
    private String id;
    /**
     * 创建时间
     */
    private Date createTime;
}
