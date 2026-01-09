package com.github.fanzezhen.fun.framework.mp.base.entity.uuid;

import com.baomidou.mybatisplus.annotation.*;
import com.github.fanzezhen.fun.framework.core.model.entity.IGenericEntity;
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
public abstract class BaseEntity implements IGenericEntity<String> {
    /**
     * 主键，使用不含“-”的UUID，只有当插入对象ID为空才自动填充
     */
    @TableId(value = "ID", type = IdType.ASSIGN_UUID)
    @Schema(name = "主键ID")
    protected String id;

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
    protected String createUserId;

    public void init(BaseEntity baseVarEntry) {
        this.id = baseVarEntry.getId();
        this.createTime = baseVarEntry.getCreateTime();
        this.createUserId = baseVarEntry.getCreateUserId();
    }

    public static String[] getFieldNames() {
        return new String[]{"id", "create_time", "create_user_id"};
    }
}
