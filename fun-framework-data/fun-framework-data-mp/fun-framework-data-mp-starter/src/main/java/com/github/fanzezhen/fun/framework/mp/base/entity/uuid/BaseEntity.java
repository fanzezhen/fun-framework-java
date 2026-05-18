package com.github.fanzezhen.fun.framework.mp.base.entity.uuid;

import com.baomidou.mybatisplus.annotation.*;
import com.github.fanzezhen.fun.framework.core.model.entity.IGenericEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * UUID主键实体基类
 * <p>
 * 提供基于UUID的主键生成策略，适用于分布式系统和需要全局唯一标识的场景。
 * 主键类型为 String（不含"-"的UUID格式），支持以下基础字段：
 * <ul>
 *   <li>id - 主键（UUID自动生成，格式为32位不含"-"的字符串）</li>
 *   <li>createTime - 创建时间（插入时自动填充）</li>
 *   <li>createUserId - 创建人ID（插入时自动填充）</li>
 * </ul>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>分布式系统</li>
 *   <li>需要与外部系统交互且要求ID可读性</li>
 *   <li>数据合并场景（避免主键冲突）</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseEntity implements IGenericEntity<String> {
    /**
     * 主键
     * <p>
     * 基于UUID生成，格式为32位不含横线的字符串。
     * 仅当插入对象ID为空时自动填充，适合分布式系统。
     */
    @TableId(type = IdType.ASSIGN_UUID)
    protected String id;

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
    protected String createUserId;

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
