package com.github.fanzezhen.fun.framework.trace.impl.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.fanzezhen.fun.framework.mp.base.entity.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.ibatis.mapping.SqlCommandType;

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
@TableName(value = "fun_trace", autoResultMap = true)
public class TraceEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

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
    private SqlCommandType type;

    /**
     * 上下文痕迹ID
     */
    private String traceId;

    /**
     * 业务表ID
     */
    private String businessId;
    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String creator;

    public TraceEntity(String id) {
        setId(id);
    }

}
