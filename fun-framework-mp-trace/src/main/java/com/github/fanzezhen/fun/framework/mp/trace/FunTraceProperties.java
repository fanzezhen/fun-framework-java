package com.github.fanzezhen.fun.framework.mp.trace;

import com.github.fanzezhen.fun.framework.mp.trace.model.bo.TraceRuleBO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 痕迹配置参数
 *
 * @author fanzezhen
 * @since 3.4.3.1
 */
@Slf4j
@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "fun.trace")
public class FunTraceProperties {
    private Map<String, TraceRuleBO> rules = new HashMap<>();
}
