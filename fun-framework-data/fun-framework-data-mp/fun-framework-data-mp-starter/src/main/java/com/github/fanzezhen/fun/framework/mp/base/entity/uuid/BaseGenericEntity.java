package com.github.fanzezhen.fun.framework.mp.base.entity.uuid;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.github.fanzezhen.fun.framework.core.model.entity.IGenericEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * UUID泛型实体基类
 * <p>
 * 在 {@link BaseEntity} 基础上扩展逻辑删除和更新审计字段，适用于需要软删除和完整审计功能的场景。
 * 主键类型为 String，包含以下扩展字段：
 * <ul>
 *   <li>delFlag - 删除标识（"0"-未删除，其他值-已删除）</li>
 *   <li>updateTime - 更新时间（插入和更新时自动填充）</li>
 *   <li>updateUserId - 更新人ID（插入和更新时自动填充）</li>
 * </ul>
 * <p>
 * 逻辑删除机制：
 * <ul>
 *   <li>删除时：delFlag 自动设置为记录的 id 值</li>
 *   <li>查询时：自动过滤 delFlag != "0" 的记录</li>
 * </ul>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseGenericEntity extends BaseEntity {

    /**
     * 删除标识
     * <p>
     * 逻辑删除字段，"0"表示未删除，其他值表示已删除（记录的id值）。
     * 插入时自动填充为"0"，删除时自动设置为记录的id。
     */
    @TableField(fill = FieldFill.INSERT)
    @TableLogic(value = IGenericEntity.DEFAULT_DEL_FLAG_STR, delval = "id")
    protected String delFlag;

    /**
     * 更新时间
     * <p>
     * 插入和更新时自动填充为当前时间。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime updateTime;

    /**
     * 更新人ID
     * <p>
     * 插入和更新时自动填充，从 {@link com.github.fanzezhen.fun.framework.core.context.ContextHolder} 获取当前登录用户ID。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected String updateUserId;

    /**
     * 判断当前记录是否已被逻辑删除
     *
     * @return true-已删除，false-未删除
     */
    public boolean isDeleted() {
        return delFlag != null && !delFlag.equals(IGenericEntity.DEFAULT_DEL_FLAG_STR);
    }

    /**
     * 获取所有字段的数据库列名数组（包含父类字段）
     *
     * @return 字段名数组
     */
    public static String[] getFieldNames() {
        return ArrayUtil.append(BaseEntity.getFieldNames(), "update_time", "update_user_id", "del_flag");
    }
}
