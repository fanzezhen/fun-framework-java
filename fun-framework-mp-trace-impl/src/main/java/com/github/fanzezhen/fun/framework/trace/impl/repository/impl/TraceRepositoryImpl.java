package com.github.fanzezhen.fun.framework.trace.impl.repository.impl;


import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.github.fanzezhen.fun.framework.trace.impl.condition.TraceSearchCondition;
import com.github.fanzezhen.fun.framework.trace.impl.dao.ITraceDao;
import com.github.fanzezhen.fun.framework.trace.impl.entity.TraceEntity;
import com.github.fanzezhen.fun.framework.trace.impl.repository.ITraceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * 痕迹表 服务实现类
 *
 * @author fanzezhen
 * @createTime 2025-01-13 17:12:18
 * @since 3.4.3.1
 */
@Slf4j
@Repository
public class TraceRepositoryImpl extends CrudRepository<ITraceDao, TraceEntity> implements ITraceRepository {

    /**
     * 根据上下文id和业务id查询
     */
    @Override
    public TraceEntity getByTraceAndBiz(String traceId, Serializable businessId) {
        return baseMapper.getByTraceAndBiz(traceId, businessId);
    }

    @Override
    public List<TraceEntity> list(TraceSearchCondition condition) {
        return baseMapper.list(condition);
    }
}
