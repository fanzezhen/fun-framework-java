package com.github.fanzezhen.fun.framework.mp.trace.service;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.fanzezhen.fun.framework.mp.trace.model.bo.TraceBO;
import com.github.fanzezhen.fun.framework.mp.trace.model.bo.TraceRuleBO;
import com.github.fanzezhen.fun.framework.mp.trace.model.bo.TraceWithDetailBO;
import com.github.fanzezhen.fun.framework.mp.trace.model.condition.TracePageCondition;
import com.github.fanzezhen.fun.framework.mp.trace.model.condition.TraceSearchCondition;
import com.github.fanzezhen.fun.framework.mp.trace.model.entity.TraceDO;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 痕迹表 服务类
 *
 * @author fanzezhen
 * @createTime 2025-01-13 17:12:18
 * @since 3.4.3.1
 */
public interface ITraceService {
    /**
     * 获取痕迹规则
     */
    TraceRuleBO getTraceRule(String tableName) ;
    /**
     * 根据上下文id和业务id查询
     */
    String getPkByTraceAndBiz(String traceId, Serializable businessId);

    /**
     * 查询
     */
    TraceWithDetailBO get(Serializable pk);

    /**
     * 分页查询
     */
    PageDTO<TraceWithDetailBO> pageWithDetail(TracePageCondition pageCondition);

    /**
     * 列表查询
     */
    List<TraceBO> list(Collection<? extends Serializable> pks);

    /**
     * 列表查询
     */
    List<TraceWithDetailBO> listWithDetail(TraceSearchCondition condition);
    
    /**
     * 保存
     */
    String save(TraceDO po) ;
    
    /**
     * 保存
     */
    int saveOrUpdateBatch(Collection<TraceDO> pos) ;
    
    /**
     * 删除
     */
    int del(Serializable pk) ;
    
    /**
     * 删除
     */
    int del(Collection<? extends Serializable> pks) ;
    
    /**
     * 转BO
     */
    default TraceBO toBO(TraceDO po) {
        return BeanUtil.copyProperties(po, TraceBO.class);
    }
    
    /**
     * 转BO
     */
    default List<TraceBO> toBO(Collection<TraceDO> pos) {
        return BeanUtil.copyToList(pos, TraceBO.class);
    }

}
