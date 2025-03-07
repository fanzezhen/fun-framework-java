package com.github.fanzezhen.fun.framework.trace.impl.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.fanzezhen.fun.framework.mp.base.IBaseMapper;
import com.github.fanzezhen.fun.framework.trace.impl.condition.TraceDetailPageCondition;
import com.github.fanzezhen.fun.framework.trace.impl.entity.TraceDetailEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 痕迹明细表 Mapper 接口
 *
 * @author fanzezhen
 * @createTime 2025-01-13 17:12:18
 * @since 3.4.3.1
 */
@Mapper
public interface ITraceDetailDao extends IBaseMapper<TraceDetailEntity> {

    /**
     * 分页查询
     */
    default PageDTO<TraceDetailEntity> page(TraceDetailPageCondition pageCondition) {
        LambdaQueryWrapper<TraceDetailEntity> wrapper = Wrappers.lambdaQuery(TraceDetailEntity.class);
        if(pageCondition.getBusinessId() != null){
            wrapper.eq(TraceDetailEntity::getBusinessId, pageCondition.getBusinessId());
        }
        if(pageCondition.getTraceId() != null){
            wrapper.eq(TraceDetailEntity::getTraceId, pageCondition.getTraceId());
        }
        if(pageCondition.getName() != null){
            wrapper.eq(TraceDetailEntity::getName, pageCondition.getName());
        }
        if(pageCondition.getCode() != null){
            wrapper.eq(TraceDetailEntity::getCode, pageCondition.getCode());
        }
        if(pageCondition.getOldValue() != null){
            wrapper.eq(TraceDetailEntity::getOldValue, pageCondition.getOldValue());
        }
        if(pageCondition.getNewValue() != null){
            wrapper.eq(TraceDetailEntity::getNewValue, pageCondition.getNewValue());
        }
        wrapper.orderByDesc(TraceDetailEntity::getCreateTime);
        return selectPage(pageCondition, wrapper);
    }
}
