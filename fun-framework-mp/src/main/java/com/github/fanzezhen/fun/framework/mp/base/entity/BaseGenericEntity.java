package com.github.fanzezhen.fun.framework.mp.base.entity;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 公共Model,将每个表都有的公共字段抽取出来
 * MappedSuperclass 注解表示不是一个完整的实体类，将不会映射到数据库表，但是它的属性都将映射到其子类的数据库字段中
 *
 * @author fanzezhen
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(indexes = {
    @Index(name = "ix_del", columnList = "DEL_FLAG")
})
public abstract class BaseGenericEntity extends BaseEntity {
    public static final Long DEFAULT_DEL_FLAG = 0L;
    static final String DEFAULT_DEL_FLAG_STR = "0";

    /**
     * 删除标识（1-已删除；0-未删除），默认 0
     */
    @Column(name = "DEL_FLAG")
    @TableField(value = "DEL_FLAG", fill = FieldFill.INSERT)
    @TableLogic(value = DEFAULT_DEL_FLAG_STR, delval = "NOW()")
    @Schema(name = "删除标识（0-未删除），默认 0")
    protected Long delFlag;

    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME")
    @TableField(value = "UPDATE_TIME", fill = FieldFill.INSERT_UPDATE)
    @Schema(name = "更新时间")
    protected LocalDateTime updateTime;

    /**
     * 更新者ID
     */
    @Column(name = "UPDATE_USER_ID")
    @TableField(value = "UPDATE_USER_ID", fill = FieldFill.INSERT_UPDATE)
    @Schema(name = "更新者ID")
    protected String updateUserId;

    public boolean isDeleted() {
        return delFlag != null && !delFlag.equals(0L);
    }

    public static String[] getFieldNames() {
        return ArrayUtil.append(BaseEntity.getFieldNames(), "update_time", "update_user_id", "del_flag");
    }
}
