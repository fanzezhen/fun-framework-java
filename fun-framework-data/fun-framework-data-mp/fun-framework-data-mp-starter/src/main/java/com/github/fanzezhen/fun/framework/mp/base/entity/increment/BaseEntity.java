package com.github.fanzezhen.fun.framework.mp.base.entity.increment;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.fanzezhen.fun.framework.core.model.entity.IGenericEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 自增主键实体基类
 * <p>
 * 提供基于数据库自增的主键生成策略，适用于单体应用或小规模分布式系统。
 * 主键类型为 Integer，支持以下基础字段：
 * <ul>
 *   <li>id - 主键（数据库自增生成）</li>
 *   <li>createTime - 创建时间（插入时自动填充）</li>
 *   <li>createUserId - 创建人ID（插入时自动填充）</li>
 * </ul>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>单体应用</li>
 *   <li>中小规模数据量的系统</li>
 *   <li>不需要分布式ID的场景</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseEntity implements IGenericEntity<Integer> {
    /**
     * 主键
     * <p>
     * 基于数据库自增生成，请确保数据库表设置了主键自增。
     * 适用于单体应用，性能开销小。
     */
    @TableId(type = IdType.AUTO)
    protected Integer id;

    /**
     * 创建时间
     * <p>
     * 插入时自动填充为当前时间。
     */
    @TableField(fill = FieldFill.INSERT)
    protected LocalDateTime createTime;

    /**
     * 创建人ID
     * <p>
     * 插入时自动填充，从 {@link com.github.fanzezhen.fun.framework.core.context.ContextHolder} 获取当前登录用户ID。
     */
    @TableField(fill = FieldFill.INSERT)
    protected Integer createUserId;

    /**
     * 从另一个实体对象初始化基础字段
     *
     * @param baseVarEntry 源实体对象
     */
    public void init(final BaseEntity baseVarEntry) {
        this.id = baseVarEntry.getId();
        this.createTime = baseVarEntry.getCreateTime();
        this.createUserId = baseVarEntry.getCreateUserId();
    }

    /**
     * 获取基础字段的数据库列名数组
     *
     * @return 字段名数组
     */
    public static String[] getFieldNames() {
        return new String[]{"id", "create_time", "create_user_id"};
    }
}
