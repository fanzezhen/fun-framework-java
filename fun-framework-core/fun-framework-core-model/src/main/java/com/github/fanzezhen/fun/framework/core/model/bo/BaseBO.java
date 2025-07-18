package com.github.fanzezhen.fun.framework.core.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公共业务模型类
 *
 * @author fanzezhen
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseBO<P extends Serializable> implements Serializable {
    @Schema(name = "主键ID")
    private P id;

    /**
     * 创建时间
     */
    @Schema(name = "创建时间")
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    @Schema(name = "创建人ID")
    private P createUserId;

    public void init(BaseBO<P> baseVarEntry) {
        this.id = baseVarEntry.getId();
        this.createTime = baseVarEntry.getCreateTime();
        this.createUserId = baseVarEntry.getCreateUserId();
    }

    public static String[] getFieldNames() {
        return new String[]{"id", "create_time", "create_user_id"};
    }

}
