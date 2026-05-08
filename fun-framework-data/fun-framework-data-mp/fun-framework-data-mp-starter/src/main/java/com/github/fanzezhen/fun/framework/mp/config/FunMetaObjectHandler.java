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
 * MyBatis-Plus字段自动填充处理器
 * <p>
 * 在插入和更新时自动填充通用字段（创建人、创建时间、更新人、更新时间、状态、删除标记等）。
 * 创建人和更新人从ContextHolder获取，避免业务代码重复设置。
 * <p>
 * <b>智能处理：</b>delFlag字段根据类型自动选择默认值（Long/Integer/String）
 *
 */
@Slf4j
@Component
public class FunMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
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
     */
    private void fillDelFlag(MetaObject metaObject) {
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


    @Override
    public void updateFill(MetaObject metaObject) {
        String loginUserId = ContextHolder.getUserId();
        if (CharSequenceUtil.isNotBlank(loginUserId)) {
            this.fillStrategy(metaObject, IGenericEntity.FIELD_UPDATE_USER_ID, loginUserId);
        }
        this.fillStrategy(metaObject, IGenericEntity.FIELD_UPDATE_TIME, LocalDateTime.now());
    }
}
