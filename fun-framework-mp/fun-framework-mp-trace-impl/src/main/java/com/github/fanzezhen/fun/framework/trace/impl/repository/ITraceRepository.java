package com.github.fanzezhen.fun.framework.trace.impl.repository;


import com.baomidou.mybatisplus.extension.repository.IRepository;
import com.github.fanzezhen.fun.framework.trace.impl.condition.TraceSearchCondition;
import com.github.fanzezhen.fun.framework.trace.impl.entity.TraceEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 痕迹表 服务类
 *
 * @author fanzezhen
 * @createTime 2025-01-13 17:12:18
 * @since 3.4.3.1
 */
public interface ITraceRepository extends IRepository<TraceEntity> {

    /**
     * 根据上下文id和业务id查询
     */
    TraceEntity getByTraceAndBiz(String traceId, Serializable businessId);

    List<TraceEntity> list(TraceSearchCondition condition);
}
