package com.github.fanzezhen.fun.framework.exception.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zezhen.fan
 */
@Slf4j
@Controller
@RequestMapping("/error")
public class ErrorController {
    /**
     * 错误页面
     */
    @GetMapping(value = "/error/{errorCode}")
    public String error(@PathVariable String errorCode) {
        return "error/" + errorCode;
    }
}
