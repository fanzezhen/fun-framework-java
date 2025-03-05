package com.github.fanzezhen.fun.framework.mp.trace.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.fanzezhen.fun.framework.mp.base.IBaseMapper;
import com.github.fanzezhen.fun.framework.mp.trace.model.condition.TraceDetailPageCondition;
import com.github.fanzezhen.fun.framework.mp.trace.model.entity.TraceDetailDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 痕迹明细表 Mapper 接口
 *
 * @author fanzezhen
 * @createTime 2025-01-13 17:12:18
 * @since 3.4.3.1
 */
@Repository
@Mapper
public interface ITraceDetailDao extends IBaseMapper<TraceDetailDO> {

    /**
     * 分页查询
     */
    default PageDTO<TraceDetailDO> page(TraceDetailPageCondition pageCondition) {
        LambdaQueryWrapper<TraceDetailDO> wrapper = Wrappers.lambdaQuery(TraceDetailDO.class);
        if(pageCondition.getBusinessId() != null){
            wrapper.eq(TraceDetailDO::getBusinessId, pageCondition.getBusinessId());
        }
        if(pageCondition.getTraceId() != null){
            wrapper.eq(TraceDetailDO::getTraceId, pageCondition.getTraceId());
        }
        if(pageCondition.getName() != null){
            wrapper.eq(TraceDetailDO::getName, pageCondition.getName());
        }
        if(pageCondition.getCode() != null){
            wrapper.eq(TraceDetailDO::getCode, pageCondition.getCode());
        }
        if(pageCondition.getOldValue() != null){
            wrapper.eq(TraceDetailDO::getOldValue, pageCondition.getOldValue());
        }
        if(pageCondition.getNewValue() != null){
            wrapper.eq(TraceDetailDO::getNewValue, pageCondition.getNewValue());
        }
        wrapper.orderByDesc(TraceDetailDO::getCreateTime);
        return selectPage(pageCondition, wrapper);
    }
}
