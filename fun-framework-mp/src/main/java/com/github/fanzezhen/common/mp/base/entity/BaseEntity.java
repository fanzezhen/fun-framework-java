package com.github.fanzezhen.fun.framework.mp.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公共Model,将每个表都有的公共字段抽取出来
 *
 * @author zezhen.fan
 * @ MappedSuperclass注解表示不是一个完整的实体类，将不会映射到数据库表，但是它的属性都将映射到其子类的数据库字段中
 */
@Entity
@Data
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @TableId(value = "ID", type = IdType.ASSIGN_UUID)
    @Schema(name = "主键ID")
    private String id;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    @Schema(name = "创建时间")
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    @Column(name = "CREATE_USER_ID")
    @TableField(value = "CREATE_USER_ID", fill = FieldFill.INSERT)
    @Schema(name = "创建人ID")
    private String createUserId;

    public void init(BaseEntity baseVarEntry) {
        this.id = baseVarEntry.getId();
        this.createTime = baseVarEntry.getCreateTime();
        this.createUserId = baseVarEntry.getCreateUserId();
    }

    public static String[] getFieldNames() {
        return new String[]{"id", "create_time", "create_user_id"};
    }

}
