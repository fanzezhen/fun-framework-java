package com.github.fanzezhen.fun.framework.mp.base.entity.uuid;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;
import com.github.fanzezhen.fun.framework.core.model.entity.IGenericEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 数据库常规实体类
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseGenericEntity extends BaseEntity {

    /**
     * 删除标识（1-已删除；0-未删除），默认 0
     */
    @TableField(fill = FieldFill.INSERT)
    @TableLogic(value = IGenericEntity.DEFAULT_DEL_FLAG_STR, delval = "id")
    @Schema(name = "删除标识（0-未删除），默认 0")
    protected String delFlag;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(name = "更新时间")
    protected LocalDateTime updateTime;

    /**
     * 更新者ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(name = "更新者ID")
    protected String updateUserId;

    public boolean isDeleted() {
        return delFlag != null && !delFlag.equals(IGenericEntity.DEFAULT_DEL_FLAG_STR);
    }

    public static String[] getFieldNames() {
        return ArrayUtil.append(BaseEntity.getFieldNames(), "update_time", "update_user_id", "del_flag");
    }
}
