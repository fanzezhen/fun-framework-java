package com.github.fanzezhen.fun.framework.mp.trace.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.fanzezhen.fun.framework.core.util.BeanCopyUtil;
import com.github.fanzezhen.fun.framework.mp.trace.model.bo.TraceDetailBO;
import com.github.fanzezhen.fun.framework.mp.trace.model.condition.TraceDetailPageCondition;
import com.github.fanzezhen.fun.framework.mp.trace.model.entity.TraceDetailDO;
import com.github.fanzezhen.fun.framework.mp.trace.model.web.request.TraceDetailPageRequest;
import com.github.fanzezhen.fun.framework.mp.trace.model.web.request.TraceDetailSaveRequest;
import com.github.fanzezhen.fun.framework.mp.trace.model.web.response.TraceDetailResponse;
import com.github.fanzezhen.fun.framework.mp.trace.service.ITraceDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Set;

/**
 * 痕迹明细表 接口
 *
 * @author fanzezhen
 * @createTime 2025-01-13 17:12:18
 * @since 3.4.3.1
 */
@Slf4j
@RestController
@RequestMapping("/trace-detail")
public class TraceDetailController {

    private final ITraceDetailService service;

    public TraceDetailController(ITraceDetailService service) {
        this.service = service;
    }

    /**
     * 查询详情
     */
    @PostMapping("/get")
    public TraceDetailResponse get(@RequestParam Serializable pk) {
        TraceDetailBO bo = service.get(pk);
        return BeanCopyUtil.copy(bo, TraceDetailResponse.class);
    }

    /**
     * 分页查询
     */
    @PostMapping("/page")
    public Page<TraceDetailDO> page(@RequestBody @Validated TraceDetailPageRequest pageRequest) {
        // 入参转换
        TraceDetailPageCondition pageCondition = BeanCopyUtil.copy(pageRequest, TraceDetailPageCondition.class);
        // 查询
        return service.page(pageCondition);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public String save(@RequestBody @Validated TraceDetailSaveRequest request) {
        TraceDetailDO po = BeanCopyUtil.copy(request, TraceDetailDO.class);
        return service.save(po);
    }

    /**
     * 删除
     */
    @PostMapping("/del")
    public Integer del(@RequestParam Serializable pk) {
        return service.del(pk);
    }

    /**
     * 批量删除
     */
    @PostMapping("/del-batch")
    public Integer del(@RequestBody @NotEmpty Set<String> pks) {
        return service.del(pks);
    }

}
