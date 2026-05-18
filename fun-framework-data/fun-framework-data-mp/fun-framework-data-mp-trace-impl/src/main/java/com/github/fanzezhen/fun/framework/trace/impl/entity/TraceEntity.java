package com.github.fanzezhen.fun.framework.trace.impl.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.ibatis.mapping.SqlCommandType;

/**
 * 数据追踪主表实体
 * <p>
 * 存储数据变更的主记录信息，包括操作类型、业务对象标识、变更时间等核心信息。
 * 通过外键关联到追踪明细表 {@link TraceDetailEntity}，形成主从关系。
 * <p>
 * 数据库表名：fun_trace<br>
 * 主键策略：雪花算法（Long 类型）
 *
 * @since 3.4.3.1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
@TableName(value = "fun_trace", autoResultMap = true)
public class TraceEntity extends BaseEntity {

    /**
     * 父级追踪ID
     * <p>
     * 用于构建追踪记录的层级关系，支持关联上级追踪记录。
     */
    private String pid;

    /**
     * 业务对象名称
     * <p>
     * 标识被追踪的业务对象类型，如"用户信息"、"订单数据"等。
     */
    private String name;

    /**
     * 追踪标识
     * <p>
     * 通常为数据库表名，用于区分不同业务对象的追踪记录。
     */
    private String code;

    /**
     * 业务对象标识值
     * <p>
     * 用于快速识别被追踪的业务数据，如用户名、订单号等关键字段的值。
     */
    private String value;

    /**
     * SQL 操作类型
     * <p>
     * 记录数据变更的操作类型（INSERT、UPDATE、DELETE）。
     * 特殊值 UNKNOWN 表示仅子表数据变更。
     */
    private SqlCommandType type;

    /**
     * 上下文追踪ID
     * <p>
     * 用于关联同一请求上下文中的多条追踪记录，支持分布式链路追踪。
     */
    private String traceId;

    /**
     * 业务主键ID
     * <p>
     * 被追踪数据的主键值，用于定位具体的业务记录。
     */
    private String businessId;

    /**
     * 创建人
     * <p>
     * 记录执行数据变更操作的用户标识，插入和更新时自动填充。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String creator;

    /**
     * 构造方法（仅设置主键）
     *
     * @param id 追踪记录ID
     */
    public TraceEntity(final Long id) {
        setId(id);
    }

}
