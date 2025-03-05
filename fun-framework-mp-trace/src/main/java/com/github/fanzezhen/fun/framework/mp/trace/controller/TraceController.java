package com.github.fanzezhen.fun.framework.mp.trace.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.fanzezhen.fun.framework.core.util.BeanCopyUtil;
import com.github.fanzezhen.fun.framework.mp.trace.model.bo.TraceWithDetailBO;
import com.github.fanzezhen.fun.framework.mp.trace.model.condition.TracePageCondition;
import com.github.fanzezhen.fun.framework.mp.trace.model.entity.TraceDO;
import com.github.fanzezhen.fun.framework.mp.trace.model.web.request.TracePageRequest;
import com.github.fanzezhen.fun.framework.mp.trace.model.web.request.TraceSaveRequest;
import com.github.fanzezhen.fun.framework.mp.trace.model.web.response.TraceResponse;
import com.github.fanzezhen.fun.framework.mp.trace.service.ITraceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Set;

/**
 * 痕迹表 接口
 *
 * @author fanzezhen
 * @since 3.4.3.1
 */
@Slf4j
@RestController
@RequestMapping("/fun/trace")
public class TraceController {

    private final ITraceService service;

    public TraceController(ITraceService service) {
        this.service = service;
    }

    /**
     * 查询详情
     */
    @GetMapping("/get")
    public TraceResponse get(@RequestParam Serializable pk) {
        TraceWithDetailBO bo = service.get(pk);
        return BeanCopyUtil.copy(bo, TraceResponse.class);
    }

    /**
     * 分页查询（带详情描述）
     */
    @PostMapping("/page-with-detail")
    public Page<TraceWithDetailBO> pageWithDetail(@RequestBody @Validated TracePageRequest pageRequest) {
        // 入参转换
        TracePageCondition pageCondition = BeanCopyUtil.copy(pageRequest, TracePageCondition.class);
        // 查询
        Page<TraceWithDetailBO> pageResult = service.pageWithDetail(pageCondition);
        // 返回值转换
        return pageResult;
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public Serializable save(@RequestBody @Validated TraceSaveRequest request) {
        TraceDO po = BeanCopyUtil.copy(request, TraceDO.class);
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
