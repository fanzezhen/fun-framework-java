package com.github.fanzezhen.fun.framework.trace.impl.controller;

import com.github.fanzezhen.fun.framework.trace.service.IFunTraceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 痕迹表 接口
 *
 * @author fanzezhen
 * @since 3.4.3.1
 */
@Slf4j
@RestController
@RequestMapping("/fun/trace")
@ConditionalOnProperty(value = "fun.trace.api.enabled", havingValue = "true")
public class TraceController {

    private final IFunTraceService service;

    public TraceController(IFunTraceService service) {
        this.service = service;
    }

}
