package com.github.fanzezhen.fun.framework.mp.base.entity.tenant;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.github.fanzezhen.fun.framework.mp.base.entity.BaseEntity;
import com.github.fanzezhen.fun.framework.mp.base.entity.BaseGenericEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * 公共Model,将每个表都有的公共字段抽取出来
 *
 * @author fanzezhen
 * @ MappedSuperclass注解表示不是一个完整的实体类，将不会映射到数据库表，但是它的属性都将映射到其子类的数据库字段中
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(indexes = {
        @Index(name = "ix_del_status", columnList = "DEL_FLAG, STATUS")
})
public abstract class BaseTenantGenericEntity extends BaseGenericEntity {

    /**
     * 租户id
     */
    @Schema(name = "租户id")
    @TableField(value = "TENANT_ID")
    @Column(name = "TENANT_ID",columnDefinition = "varchar(50) not null comment '租户id'", nullable = false)
    protected String tenantId;

    public static String[] getFieldNames() {
        return ArrayUtil.append(BaseEntity.getFieldNames(), "TENANT_ID");
    }
}
