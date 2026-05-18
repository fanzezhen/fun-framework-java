package com.github.fanzezhen.fun.framework.core.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公共业务模型类
 * <p>
 * 提供业务对象(Business Object)的基础属性，包括主键、创建时间和创建人ID。
 * 所有业务对象应继承此类以获得统一的基础字段定义。
 * </p>
 *
 * @param <P> 主键类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseBO<P extends Serializable> implements Serializable {
    /**
     * 主键
     */
    private P id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    private P createUserId;

    /**
     * 从另一个 BaseBO 对象初始化当前对象的基础字段
     *
     * @param baseVarEntry 源对象
     */
    public void init(BaseBO<P> baseVarEntry) {
        this.id = baseVarEntry.getId();
        this.createTime = baseVarEntry.getCreateTime();
        this.createUserId = baseVarEntry.getCreateUserId();
    }

    /**
     * 获取基础字段名数组
     * <p>
     * 返回 BaseBO 中定义的所有数据库字段名（下划线格式）
     * </p>
     *
     * @return 基础字段名数组
     */
    public static String[] getFieldNames() {
        return new String[]{"id", "create_time", "create_user_id"};
    }

}
