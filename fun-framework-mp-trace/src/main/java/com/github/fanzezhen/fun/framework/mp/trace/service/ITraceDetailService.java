package com.github.fanzezhen.fun.framework.mp.trace.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.fanzezhen.fun.framework.mp.trace.model.bo.TraceDetailBO;
import com.github.fanzezhen.fun.framework.mp.trace.model.condition.TraceDetailCondition;
import com.github.fanzezhen.fun.framework.mp.trace.model.condition.TraceDetailPageCondition;
import com.github.fanzezhen.fun.framework.mp.trace.model.entity.TraceDetailDO;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 痕迹明细表 服务类
 *
 * @author fanzezhen
 * @createTime 2025-01-13 17:12:18
 * @since 3.4.3.1
 */
public interface ITraceDetailService {

    /**
     * 查询
     */
    TraceDetailBO get(Serializable pk);

    /**
     * 分页查询
     */
    PageDTO<TraceDetailDO> page(TraceDetailPageCondition pageCondition);

    /**
     * 列表查询
     */
    List<TraceDetailBO> listByTrace(Serializable traceId);
    /**
     * 列表查询
     */
    List<TraceDetailBO> listByTrace(Collection<? extends Serializable> traceIds);
    /**
     * 映射查询
     */
    Map<Long, List<TraceDetailBO>> mapByTrace(Collection<? extends Serializable> traceIds);

    /**
     * 列表查询
     */
    List<TraceDetailBO> list(Collection<? extends Serializable> pks);
    
    /**
     * 保存
     */
    String save(TraceDetailDO po) ;
    
    /**
     * 保存
     */
    int saveOrUpdateBatch(Collection<TraceDetailDO> pos) ;
    
    /**
     * 删除
     */
    int del(Serializable pk) ;
    
    /**
     * 删除
     */
    int del(Collection<? extends Serializable> pks) ;

    /**
     * 查询列表
     */
    default List<TraceDetailDO> list(TraceDetailCondition condition) {
        TraceDetailPageCondition pageCondition = BeanUtil.copyProperties(condition, TraceDetailPageCondition.class);
        pageCondition.setCurrent(1L);
        pageCondition.setSize(Long.MAX_VALUE);
        PageDTO<TraceDetailDO> pageResult = page(pageCondition);
        return pageResult != null && pageResult.getRecords() != null ? pageResult.getRecords() : Collections.emptyList();
    }
    
    /**
     * 转BO
     */
    default TraceDetailBO toBO(TraceDetailDO po) {
        return BeanUtil.copyProperties(po, TraceDetailBO.class);
    }
    
    /**
     * 转BO
     */
    default List<TraceDetailBO> toBO(Collection<TraceDetailDO> pos) {
        return BeanUtil.copyToList(pos, TraceDetailBO.class);
    }

}
