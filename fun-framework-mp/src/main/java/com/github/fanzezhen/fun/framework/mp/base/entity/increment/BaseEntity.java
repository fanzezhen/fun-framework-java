package com.github.fanzezhen.fun.framework.mp.base.entity.increment;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.fanzezhen.fun.framework.core.model.entity.IBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 公共数据库实体类
 *
 * @author fanzezhen
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseEntity implements IBaseEntity<Integer> {
    /**
     * 自增主键，请确保数据库设置了 ID自增
     */
    @TableId(value = "ID", type = IdType.AUTO)
    @Schema(name = "主键ID")
    protected Integer id;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    @Schema(name = "创建时间")
    protected LocalDateTime createTime;

    /**
     * 创建人ID
     */
    @TableField(value = "CREATE_USER_ID", fill = FieldFill.INSERT)
    @Schema(name = "创建人ID")
    protected Integer createUserId;

    public void init(BaseEntity baseVarEntry) {
        this.id = baseVarEntry.getId();
        this.createTime = baseVarEntry.getCreateTime();
        this.createUserId = baseVarEntry.getCreateUserId();
    }

    public static String[] getFieldNames() {
        return new String[]{"id", "create_time", "create_user_id"};
    }
}
