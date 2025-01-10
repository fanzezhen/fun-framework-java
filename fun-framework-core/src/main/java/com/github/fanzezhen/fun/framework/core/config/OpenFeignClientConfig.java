package com.github.fanzezhen.fun.framework.core.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import com.github.fanzezhen.fun.framework.core.context.SysContext;
import com.github.fanzezhen.fun.framework.core.context.SysContextHolder;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Iterator;
import java.util.List;

/**
 * @author zezhen.fan
 * @date 2023/8/16
 */
@Slf4j
@Configuration
@ConditionalOnClass(
        name = {"org.springframework.cloud.openfeign.EnableFeignClients"}
)
public class OpenFeignClientConfig {
    @Bean
    public RequestInterceptor systemContextInterceptor() {
        return requestTemplate -> {
            SysContext systemContext = SysContextHolder.getSysContext();
            if (systemContext != null) {
                List<Pair<String, String>> pairs = systemContext.toHeaders();
                Pair<String, String> pair;
                if (CollUtil.isNotEmpty(pairs)) {
                    for (Iterator<Pair<String, String>> var3 = pairs.iterator(); var3.hasNext(); requestTemplate.header(pair.getKey(), pair.getValue())) {
                        pair = var3.next();
                        if (log.isDebugEnabled()) {
                            log.debug("add header:{},value:{} to feign request", pair.getKey(), pair.getValue());
                        }
                    }
                }
            }
        };
    }
}
