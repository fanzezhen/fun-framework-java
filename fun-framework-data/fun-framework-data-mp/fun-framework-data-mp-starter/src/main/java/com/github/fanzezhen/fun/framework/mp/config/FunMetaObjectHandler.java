package com.github.fanzezhen.fun.framework.mp.config;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.github.fanzezhen.fun.framework.core.context.ContextHolder;
import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;
import com.github.fanzezhen.fun.framework.core.model.entity.IGenericEntity;
import com.github.fanzezhen.fun.framework.mp.enums.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 字段自动填充处理器
 * <p>
 * 在插入和更新时自动填充通用字段，包括创建人、创建时间、更新人、更新时间、状态、删除标记等。
 * 创建人和更新人从 {@link ContextHolder} 获取，避免业务代码重复设置。
 * <p>
 * 智能处理：
 * <ul>
 *   <li>delFlag 字段根据类型（Long/Integer/String）自动选择默认值</li>
 *   <li>status 字段默认填充为 {@link StatusEnum#ENABLE}</li>
 *   <li>时间字段使用 {@link LocalDateTime#now()}</li>
 * </ul>
 */
@Slf4j
@Component
public class FunMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作时的字段自动填充
     * <p>
     * 自动填充以下字段：
     * <ul>
     *   <li>createUserId - 创建人ID（从 ContextHolder 获取）</li>
     *   <li>delFlag - 删除标记（根据字段类型选择默认值）</li>
     *   <li>status - 状态（默认启用）</li>
     *   <li>createTime - 创建时间</li>
     *   <li>updateTime - 更新时间</li>
     * </ul>
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(final MetaObject metaObject) {
        String loginUserId = ContextHolder.getUserId();
        if (CharSequenceUtil.isNotBlank(loginUserId)) {
            this.fillStrategy(metaObject, IGenericEntity.FIELD_CREATE_USER_ID, loginUserId);
        }
        this.fillDelFlag(metaObject);
        this.fillStrategy(metaObject, IGenericEntity.FIELD_STATUS, StatusEnum.ENABLE);
        this.fillStrategy(metaObject, IGenericEntity.FIELD_CREATE_TIME, LocalDateTime.now());
        this.fillStrategy(metaObject, IGenericEntity.FIELD_UPDATE_TIME, LocalDateTime.now());
    }

    /**
     * 智能填充 delFlag 字段
     * <p>
     * 根据字段类型自动选择合适的默认值：
     * <ul>
     *   <li>Long/long - 填充 0L</li>
     *   <li>Integer/int - 填充 0</li>
     *   <li>String - 填充 "0"</li>
     * </ul>
     *
     * @param metaObject 元对象
     */
    private void fillDelFlag(final MetaObject metaObject) {
        if (!metaObject.hasSetter(IGenericEntity.FIELD_DEL_FLAG)) {
            return;
        }
        Class<?> fieldType = metaObject.getSetterType(IGenericEntity.FIELD_DEL_FLAG);
        if (Long.class.equals(fieldType) || long.class.equals(fieldType)) {
            this.fillStrategy(metaObject, IGenericEntity.FIELD_DEL_FLAG, IGenericEntity.DEFAULT_DEL_FLAG_LONG);
        } else if (Integer.class.equals(fieldType) || int.class.equals(fieldType)) {
            this.fillStrategy(metaObject, IGenericEntity.FIELD_DEL_FLAG, IGenericEntity.DEFAULT_DEL_FLAG_INT);
        } else if (String.class.equals(fieldType)) {
            this.fillStrategy(metaObject, IGenericEntity.FIELD_DEL_FLAG, IGenericEntity.DEFAULT_DEL_FLAG_STR);
        } else {
            log.warn("实体类的 delFlag 字段类型 [{}] 不是预期的 Integer 或 Long", fieldType.getName());
        }
    }


    /**
     * 更新操作时的字段自动填充
     * <p>
     * 自动填充以下字段：
     * <ul>
     *   <li>updateUserId - 更新人ID（从 ContextHolder 获取）</li>
     *   <li>updateTime - 更新时间</li>
     * </ul>
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(final MetaObject metaObject) {
        String loginUserId = ContextHolder.getUserId();
        if (CharSequenceUtil.isNotBlank(loginUserId)) {
            this.fillStrategy(metaObject, IGenericEntity.FIELD_UPDATE_USER_ID, loginUserId);
        }
        this.fillStrategy(metaObject, IGenericEntity.FIELD_UPDATE_TIME, LocalDateTime.now());
    }
}
