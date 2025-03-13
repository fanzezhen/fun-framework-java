package com.github.fanzezhen.fun.framework.mp.config;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.github.fanzezhen.fun.framework.core.context.SysContextHolder;
import com.github.fanzezhen.fun.framework.mp.base.entity.BaseGenericEntity;
import com.github.fanzezhen.fun.framework.mp.enums.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author fanzezhen
 */
@Slf4j
@Component
public class FunMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        String loginUserId = SysContextHolder.getUserId();
        if (CharSequenceUtil.isNotBlank(loginUserId)) {
            this.fillStrategy(metaObject, "createUserId", loginUserId);
        }
        this.fillStrategy(metaObject, "status", StatusEnum.ENABLE);
        this.fillStrategy(metaObject, "delFlag", BaseGenericEntity.DEFAULT_DEL_FLAG);
        this.fillStrategy(metaObject, "createTime", LocalDateTime.now());
        this.fillStrategy(metaObject, "updateTime", LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String loginUserId = SysContextHolder.getUserId();
        if (CharSequenceUtil.isNotBlank(loginUserId)) {
            this.fillStrategy(metaObject, "updateUserId", loginUserId);
        }
        this.fillStrategy(metaObject, "updateTime", LocalDateTime.now());
    }
}
