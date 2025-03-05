package com.github.fanzezhen.fun.framework.mp.trace.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.fanzezhen.fun.framework.mp.trace.FunTraceProperties;
import com.github.fanzezhen.fun.framework.mp.trace.dao.ITraceDao;
import com.github.fanzezhen.fun.framework.mp.trace.model.bo.TraceBO;
import com.github.fanzezhen.fun.framework.mp.trace.model.bo.TraceDetailBO;
import com.github.fanzezhen.fun.framework.mp.trace.model.bo.TraceRuleBO;
import com.github.fanzezhen.fun.framework.mp.trace.model.condition.TracePageCondition;
import com.github.fanzezhen.fun.framework.mp.trace.model.entity.TraceDO;
import com.github.fanzezhen.fun.framework.mp.trace.service.ITraceService;
import com.github.fanzezhen.fun.framework.mp.trace.service.ITraceDetailService;
import com.github.fanzezhen.fun.framework.mp.trace.model.bo.TraceWithDetailBO;
import com.github.fanzezhen.fun.framework.mp.trace.model.condition.TraceSearchCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 痕迹表 服务实现类
 *
 * @author fanzezhen
 * @createTime 2025-01-13 17:12:18
 * @since 3.4.3.1
 */
@Slf4j
@Service
public class TraceServiceImpl implements ITraceService {
    @Resource
    private ITraceDao dao;
    @Resource
    private ITraceDetailService traceDetailService;
    @Resource
    private FunTraceProperties funTraceProperties;
    @Value("${mybatis-plus.global-config.db-config.table-prefix:}")
    private String tablePrefix;

    /**
     * 获取痕迹规则
     */
    @Override
    public TraceRuleBO getTraceRule(String tableName) {
        return funTraceProperties.getRules().get(tableName);
    }

    /**
     * 根据上下文id和业务id查询
     */
    @Override
    public String getPkByTraceAndBiz(String traceId, Serializable businessId) {
        TraceDO po = dao.getByTraceAndBiz(traceId, businessId);
        return po != null ? po.getId() : null;
    }

    /**
     * 查询
     */
    @Override
    public TraceWithDetailBO get(Serializable pk) {
        TraceDO po = dao.selectById(pk);
        TraceWithDetailBO bo = BeanUtil.copyProperties(po, TraceWithDetailBO.class);
        bo.setDetailList(traceDetailService.listByTrace(bo.getId()));
        return bo;
    }

    /**
     * 分页查询
     */
    @Override
    public PageDTO<TraceWithDetailBO> pageWithDetail(TracePageCondition pageCondition) {
        PageDTO<TraceDO> poPageDTO = dao.page(pageCondition);
        PageDTO<TraceWithDetailBO> boPageDTO = new PageDTO<>();
        BeanUtil.copyProperties(poPageDTO, boPageDTO);
        if (boPageDTO != null && CollUtil.isNotEmpty(boPageDTO.getRecords())) {
            Map<Long, List<TraceDetailBO>> map = traceDetailService.mapByTrace(boPageDTO.getRecords().stream().map(TraceWithDetailBO::getId).collect(Collectors.toList()));
            for (TraceWithDetailBO bo : boPageDTO.getRecords()) {
                bo.setDetailList(map.get(bo.getId()));
            }
        }
        return boPageDTO;
    }

    /**
     * 列表查询
     */
    @Override
    public List<TraceBO> list(Collection<? extends Serializable> pks) {
        List<TraceDO> poList = dao.list(pks);
        return BeanUtil.copyToList(poList, TraceBO.class);
    }

    /**
     * 列表查询
     */
    @Override
    public List<TraceWithDetailBO> listWithDetail(TraceSearchCondition condition) {
        List<TraceDO> poList = dao.list(condition);
        List<TraceWithDetailBO> boList = BeanUtil.copyToList(poList, TraceWithDetailBO.class);
        if (CollUtil.isNotEmpty(boList)) {
            Map<Long, List<TraceDetailBO>> map = traceDetailService.mapByTrace(boList.stream().map(TraceWithDetailBO::getId).collect(Collectors.toList()));
            for (TraceWithDetailBO bo : boList) {
                bo.setDetailList(map.get(bo.getId()));
            }
        }
        return boList;
    }

    /**
     * 保存
     */
    @Override
    public String save(TraceDO po) {
        return dao.save(po);
    }

    /**
     * 保存
     */
    @Override
    @Transactional
    public int saveOrUpdateBatch(Collection<TraceDO> pos) {
        return dao.save(pos);
    }

    /**
     * 删除
     */
    @Override
    public int del(Serializable pk) {
        return dao.deleteById(pk);
    }

    /**
     * 删除
     */
    @Override
    public int del(Collection<? extends Serializable> pks) {
        return dao.deleteBatchIds(pks);
    }
}
