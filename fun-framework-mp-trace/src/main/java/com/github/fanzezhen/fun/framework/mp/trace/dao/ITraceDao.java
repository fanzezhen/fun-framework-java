package com.github.fanzezhen.fun.framework.mp.trace.dao;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.fanzezhen.fun.framework.mp.base.IBaseMapper;
import com.github.fanzezhen.fun.framework.mp.trace.model.condition.TracePageCondition;
import com.github.fanzezhen.fun.framework.mp.trace.model.condition.TraceSearchCondition;
import com.github.fanzezhen.fun.framework.mp.trace.model.entity.TraceDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

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
@Repository
@Mapper
public interface ITraceDao extends IBaseMapper<TraceDO> {
    /**
     * 根据上下文id和业务id查询
     */
    default TraceDO getByTraceAndBiz(String traceId, Serializable businessId) {
        LambdaQueryWrapper<TraceDO> wrapper = Wrappers.lambdaQuery(TraceDO.class)
            .eq(TraceDO::getTraceId, traceId)
            .eq(TraceDO::getBusinessId, businessId);
        return selectOne(wrapper, false);
    }
    /**
     * 分页查询
     */
    default PageDTO<TraceDO> page(TracePageCondition pageCondition) {
        LambdaQueryWrapper<TraceDO> wrapper = Wrappers.lambdaQuery(TraceDO.class);
        if (pageCondition.getName() != null) {
            wrapper.eq(TraceDO::getName, pageCondition.getName());
        }
        if (pageCondition.getCode() != null) {
            wrapper.eq(TraceDO::getCode, pageCondition.getCode());
        }
        if (pageCondition.getValue() != null) {
            wrapper.eq(TraceDO::getValue, pageCondition.getValue());
        }
        if (pageCondition.getType() != null) {
            wrapper.eq(TraceDO::getType, pageCondition.getType());
        }
        if (pageCondition.getTraceId() != null) {
            wrapper.eq(TraceDO::getTraceId, pageCondition.getTraceId());
        }
        if (pageCondition.getBusinessId() != null) {
            wrapper.eq(TraceDO::getBusinessId, pageCondition.getBusinessId());
        }
        if (pageCondition.getBeginTime() != null) {
            wrapper.ge(TraceDO::getCreateTime, pageCondition.getBeginTime());
        }
        if (pageCondition.getEndTime() != null) {
            wrapper.le(TraceDO::getCreateTime, pageCondition.getEndTime());
        }
        if (pageCondition.getCreateBy() != null) {
            wrapper.eq(TraceDO::getCreateUserId, pageCondition.getCreateBy());
        }
        if (CharSequenceUtil.isNotEmpty(pageCondition.getKeyword())) {
            wrapper.and(childWrapper -> childWrapper
                .like(TraceDO::getCode, pageCondition.getKeyword()).or()
                .like(TraceDO::getName, pageCondition.getKeyword()).or()
                .like(TraceDO::getValue, pageCondition.getKeyword()));
        }
        wrapper.orderByDesc(TraceDO::getCreateTime);
        return selectPage(pageCondition, wrapper);
    }
    /**
     * 列表查询
     */
    default List<TraceDO> list(TraceSearchCondition condition) {
        TracePageCondition pageCondition = BeanUtil.copyProperties(condition, TracePageCondition.class);
        pageCondition.setCurrent(1L);
        pageCondition.setSize(Long.MAX_VALUE);
        PageDTO<TraceDO> pageResult = page(pageCondition);
        return pageResult != null ? pageResult.getRecords() : Collections.emptyList();
    }

}
