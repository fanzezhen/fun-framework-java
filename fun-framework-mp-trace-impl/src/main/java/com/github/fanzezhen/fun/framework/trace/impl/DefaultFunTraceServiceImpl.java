package com.github.fanzezhen.fun.framework.trace.impl;

import com.baomidou.mybatisplus.extension.repository.IRepository;
import com.github.fanzezhen.fun.framework.trace.impl.entity.TraceDetailEntity;
import com.github.fanzezhen.fun.framework.trace.impl.entity.TraceEntity;
import com.github.fanzezhen.fun.framework.trace.impl.repository.ITraceDetailRepository;
import com.github.fanzezhen.fun.framework.trace.impl.repository.ITraceRepository;
import com.github.fanzezhen.fun.framework.trace.FunTraceProperties;
import com.github.fanzezhen.fun.framework.trace.model.bo.TraceRuleBO;
import com.github.fanzezhen.fun.framework.trace.service.IFunTraceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * 痕迹表 服务实现类
 *
 * @author fanzezhen
 * @since 3.4.3.1
 */
@Slf4j
@Service
@ConditionalOnMissingBean(value = IFunTraceService.class, ignored = DefaultFunTraceServiceImpl.class)
public class DefaultFunTraceServiceImpl implements IFunTraceService<Long, TraceEntity, TraceDetailEntity> {
    @Resource
    private ITraceRepository dao;
    @Resource
    private ITraceDetailRepository traceDetailService;
    @Resource
    private FunTraceProperties funTraceProperties;

    /**
     * 获取痕迹规则
     */
    @Override
    public TraceRuleBO getTraceRule(String tableName) {
        return funTraceProperties.getRules().get(tableName);
    }

    /**
     * 保存
     */
    @Override
    public boolean addTrace(Collection<TraceEntity> traces) {
        return dao.saveBatch(traces, IRepository.DEFAULT_BATCH_SIZE);
    }

    /**
     * 保存
     */
    @Override
    @Transactional
    public boolean addDetail(Collection<TraceDetailEntity> details) {
        return traceDetailService.saveOrUpdateBatch(details);
    }

    /**
     * 为明细数据设置痕迹id
     */
    @Override
    public TraceDetailEntity setTraceId(TraceDetailEntity detail, Long traceId) {
        return detail.setTraceId(traceId);
    }

    /**
     * 创建痕迹实体
     *
     * @param type       痕迹类型
     * @param businessId 业务id
     * @param name       名称
     * @param code       标识
     * @param value      值
     */
    @Override
    public TraceEntity newTrace(SqlCommandType type, String businessId, String name, String code, String value) {
        return new TraceEntity().setType(type).setBusinessId(businessId).setName(name).setCode(code).setValue(value);
    }

    /**
     * 创建痕迹明细实体
     *
     * @param businessId 业务id
     * @param name       名称
     * @param code       标识
     * @param newValue   新值
     */
    @Override
    public TraceDetailEntity newTraceDetail(String businessId, String name, String code, String newValue) {
        return new TraceDetailEntity().setBusinessId(businessId).setName(name).setCode(code).setNewValue(newValue);
    }

}
