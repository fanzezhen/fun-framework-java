package com.github.fanzezhen.fun.framework.core.thread;

import com.alibaba.fastjson.JSONObject;
import com.github.fanzezhen.fun.framework.core.context.SysContextHolder;
import org.springframework.core.annotation.Order;

/**
 * 上下文装饰器
 *
 * @author fanzezhen
 * @createTime 2024/2/1 19:05
 * @since 3.1.7
 */
@Order
public class SysContextTaskDecorator implements ThreadPoolTaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        JSONObject contextMap = SysContextHolder.getContextMap();
        return () -> {
            try {
                SysContextHolder.setContextMap(contextMap);
                runnable.run();
            } finally {
                SysContextHolder.clean();
            }
        };
    }
}
