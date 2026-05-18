package com.github.fanzezhen.fun.framework.trace;

import com.github.fanzezhen.fun.framework.trace.model.bo.TraceRuleBO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据追踪配置属性类
 * <p>
 * 管理数据变更追踪的规则配置，通过 {@code fun.trace} 前缀绑定配置文件。
 * 支持为不同表配置独立的追踪规则，实现灵活的数据审计能力。
 * <p>
 * 配置示例：
 * <pre>
 * fun:
 *   trace:
 *     rules:
 *       sys_user:
 *         name: 用户信息
 *         nameKey: username
 *         detail:
 *           email: 邮箱
 *           phone: 手机号
 * </pre>
 *
 * @since 3.4.3.1
 */
@Slf4j
@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "fun.trace")
public class FunTraceProperties {
    /**
     * 追踪规则映射表
     * <p>
     * key: 数据库表名<br>
     * value: 该表对应的追踪规则（包含追踪字段、显示名称等配置）
     */
    private Map<String, TraceRuleBO> rules = new HashMap<>();

}
