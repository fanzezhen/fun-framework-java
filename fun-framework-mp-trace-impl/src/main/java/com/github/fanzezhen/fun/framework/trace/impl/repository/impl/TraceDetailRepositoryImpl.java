package com.github.fanzezhen.fun.framework.trace.impl.repository.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.github.fanzezhen.fun.framework.trace.impl.dao.ITraceDetailDao;
import com.github.fanzezhen.fun.framework.trace.impl.repository.ITraceDetailRepository;
import com.github.fanzezhen.fun.framework.trace.impl.model.bo.TraceDetailBO;
import com.github.fanzezhen.fun.framework.trace.impl.condition.TraceDetailPageCondition;
import com.github.fanzezhen.fun.framework.trace.impl.entity.TraceDetailEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 痕迹明细表 服务实现类
 *
 * @author fanzezhen
 * @createTime 2025-01-13 17:12:18
 * @since 3.4.3.1
 */
@Slf4j
@Service
public class TraceDetailRepositoryImpl extends CrudRepository<ITraceDetailDao, TraceDetailEntity> implements ITraceDetailRepository {
    @Resource
    private ITraceDetailDao dao;

    /**
     * 查询
     */
    @Override
    public TraceDetailBO get(Serializable pk) {
        TraceDetailEntity po = dao.selectById(pk);
        return toBO(po);
    }

    /**
     * 分页查询
     */
    @Override
    public PageDTO<TraceDetailEntity> page(TraceDetailPageCondition pageCondition) {
        return dao.page(pageCondition);
    }

    /**
     * 列表查询
     */
    @Override
    public List<TraceDetailBO> listByTrace(Serializable traceId) {
        List<TraceDetailEntity> poList = dao.listByColumn(TraceDetailEntity::getTraceId, traceId);
        return BeanUtil.copyToList(poList, TraceDetailBO.class);
    }

    /**
     * 列表查询
     */
    @Override
    public List<TraceDetailBO> listByTrace(Collection<? extends Serializable> traceIds) {
        List<TraceDetailEntity> poList = dao.listByColumn(TraceDetailEntity::getTraceId, traceIds);
        return BeanUtil.copyToList(poList, TraceDetailBO.class);
    }

    /**
     * 映射查询
     */
    @Override
    public Map<String, List<TraceDetailBO>> mapByTrace(Collection<? extends Serializable> traceIds) {
        return listByTrace(traceIds).stream().collect(Collectors.groupingBy(TraceDetailBO::getTraceId));
    }

    /**
     * 列表查询
     */
    @Override
    public List<TraceDetailBO> list(Collection<? extends Serializable> pks) {
        List<TraceDetailEntity> poList = dao.list(pks);
        return toBO(poList);
    }

    /**
     * 保存
     */
    @Override
    @Transactional
    public boolean saveOrUpdateBatch(Collection<TraceDetailEntity> pos) {
        dao.save(pos);
        return true;
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
        return dao.deleteByIds(pks);
    }
}
