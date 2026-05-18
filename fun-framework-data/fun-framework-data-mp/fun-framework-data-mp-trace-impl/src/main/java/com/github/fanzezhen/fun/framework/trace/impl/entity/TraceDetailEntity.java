package com.github.fanzezhen.fun.framework.trace.impl.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据追踪明细表实体
 * <p>
 * 存储字段级别的数据变更详情，记录每个字段的变更前后值。
 * 通过 traceId 外键关联到追踪主表 {@link TraceEntity}。
 * <p>
 * 数据库表名：fun_trace_detail<br>
 * 主键策略：雪花算法（Long 类型）
 *
 * @since 3.4.3.1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
@TableName(value = "fun_trace_detail", autoResultMap = true)
public class TraceDetailEntity extends BaseEntity {

    /**
     * 业务主键ID
     * <p>
     * 被追踪数据的主键值，用于定位具体的业务记录。
     */
    private String businessId;

    /**
     * 追踪主表ID
     * <p>
     * 外键关联到 fun_trace 表的主键，建立主从关系。
     */
    private Long traceId;

    /**
     * 字段显示名称
     * <p>
     * 字段在业务系统中的显示名称，如"邮箱"、"手机号"等，便于审计日志展示。
     */
    private String name;

    /**
     * 字段标识
     * <p>
     * 数据库列名，用于技术层面定位具体字段。
     */
    private String code;

    /**
     * 旧值
     * <p>
     * 字段变更前的值，INSERT 操作时为空。
     */
    private String oldValue;

    /**
     * 新值
     * <p>
     * 字段变更后的值，DELETE 操作时为空。
     */
    private String newValue;

    /**
     * 构造方法（仅设置主键）
     *
     * @param id 追踪明细ID
     */
    public TraceDetailEntity(final Long id) {
        setId(id);
    }
}
