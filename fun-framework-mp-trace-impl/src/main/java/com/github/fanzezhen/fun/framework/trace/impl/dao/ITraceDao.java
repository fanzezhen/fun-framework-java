package com.github.fanzezhen.fun.framework.trace.impl.dao;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.fanzezhen.fun.framework.mp.base.IBaseMapper;
import com.github.fanzezhen.fun.framework.trace.impl.condition.TracePageCondition;
import com.github.fanzezhen.fun.framework.trace.impl.condition.TraceSearchCondition;
import com.github.fanzezhen.fun.framework.trace.impl.entity.TraceEntity;
import org.apache.ibatis.annotations.Mapper;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 痕迹表 Mapper 接口
 *
 * @author fanzezhen
 * @createTime 2025-01-13 17:12:18
 * @since 3.4.3.1
 */
@Mapper
public interface ITraceDao extends IBaseMapper<TraceEntity> {
    /**
     * 根据上下文id和业务id查询
     */
    default TraceEntity getByTraceAndBiz(String traceId, Serializable businessId) {
        LambdaQueryWrapper<TraceEntity> wrapper = Wrappers.lambdaQuery(TraceEntity.class)
            .eq(TraceEntity::getTraceId, traceId)
            .eq(TraceEntity::getBusinessId, businessId);
        return selectOne(wrapper, false);
    }

    /**
     * 分页查询
     */
    default PageDTO<TraceEntity> page(TracePageCondition pageCondition) {
        LambdaQueryWrapper<TraceEntity> wrapper = Wrappers.lambdaQuery(TraceEntity.class);
        if (pageCondition.getName() != null) {
            wrapper.eq(TraceEntity::getName, pageCondition.getName());
        }
        if (pageCondition.getCode() != null) {
            wrapper.eq(TraceEntity::getCode, pageCondition.getCode());
        }
        if (pageCondition.getValue() != null) {
            wrapper.eq(TraceEntity::getValue, pageCondition.getValue());
        }
        if (pageCondition.getType() != null) {
            wrapper.eq(TraceEntity::getType, pageCondition.getType());
        }
        if (pageCondition.getTraceId() != null) {
            wrapper.eq(TraceEntity::getTraceId, pageCondition.getTraceId());
        }
        if (pageCondition.getBusinessId() != null) {
            wrapper.eq(TraceEntity::getBusinessId, pageCondition.getBusinessId());
        }
        if (pageCondition.getBeginTime() != null) {
            wrapper.ge(TraceEntity::getCreateTime, pageCondition.getBeginTime());
        }
        if (pageCondition.getEndTime() != null) {
            wrapper.le(TraceEntity::getCreateTime, pageCondition.getEndTime());
        }
        if (pageCondition.getCreateUserId() != null) {
            wrapper.eq(TraceEntity::getCreateUserId, pageCondition.getCreateUserId());
        }
        if (CharSequenceUtil.isNotEmpty(pageCondition.getKeyword())) {
            wrapper.and(childWrapper -> childWrapper
                .like(TraceEntity::getCode, pageCondition.getKeyword()).or()
                .like(TraceEntity::getName, pageCondition.getKeyword()).or()
                .like(TraceEntity::getValue, pageCondition.getKeyword()));
        }
        wrapper.orderByDesc(TraceEntity::getCreateTime);
        return selectPage(pageCondition, wrapper);
    }

    /**
     * 列表查询
     */
    default List<TraceEntity> list(TraceSearchCondition condition) {
        TracePageCondition pageCondition = BeanUtil.copyProperties(condition, TracePageCondition.class);
        pageCondition.setCurrent(1L);
        pageCondition.setSize(Long.MAX_VALUE);
        PageDTO<TraceEntity> pageResult = page(pageCondition);
        return pageResult != null ? pageResult.getRecords() : Collections.emptyList();
    }

}
