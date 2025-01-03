package com.github.fanzezhen.fun.framework.mp.interceptor.operate.facade;

import com.github.fanzezhen.fun.framework.mp.interceptor.operate.model.LogOperationDetailDto;
import com.github.fanzezhen.fun.framework.mp.interceptor.operate.model.LogOperationDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

public interface LogFacade {

    /**
     * 加载用户信息
     *
     * @param logOperation 日志封装
     * @return 日志ID
     */
    @GetMapping("/add/operate")
    String addLogOperate(@RequestBody LogOperationDto logOperation);

    /**
     * 加载用户信息
     *
     * @param logOperationDetails 日志封装
     * @return Boolean
     */
    @GetMapping("/add/operate-detail/batch")
    boolean addLogOperateDetailBatch(@RequestBody Collection<LogOperationDetailDto> logOperationDetails);
}
