package com.github.fanzezhen.fun.framework.sentinel;

import cn.hutool.setting.dialect.Props;
import com.alibaba.csp.sentinel.datasource.FileRefreshableDataSource;
import com.alibaba.csp.sentinel.datasource.FileWritableDataSource;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.csp.sentinel.transport.util.WritableDataSourceRegistry;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

/**
 * Sentinel 文件数据源初始化器。
 * <p>
 * 通过 Sentinel 的 SPI 机制自动注册基于文件的规则数据源，支持：
 * <ul>
 *   <li>流控规则（FlowRule）</li>
 *   <li>降级规则（DegradeRule）</li>
 *   <li>系统规则（SystemRule）</li>
 *   <li>授权规则（AuthorityRule）</li>
 * </ul>
 * <p>
 * 使用方法：在 META-INF/services/com.alibaba.csp.sentinel.init.InitFunc 文件中添加本类全限定名。
 * <p>
 * 配置项（通过 sentinel.properties 文件）：
 * <ul>
 *   <li>com.github.fanzezhen.fun.framework.core.common.sentinel.file.dir - 规则文件目录</li>
 *   <li>com.github.fanzezhen.fun.framework.core.common.sentinel.file.flow-rule - 流控规则文件名</li>
 *   <li>com.github.fanzezhen.fun.framework.core.common.sentinel.file.degrade-rule - 降级规则文件名</li>
 *   <li>com.github.fanzezhen.fun.framework.core.common.sentinel.file.system-rule - 系统规则文件名</li>
 *   <li>com.github.fanzezhen.fun.framework.core.common.sentinel.file.authority-rule - 授权规则文件名</li>
 * </ul>
 */
@Slf4j
public class FileDataSourceInit implements InitFunc {

    /**
     * 默认规则文件目录。
     */
    private static final String DEFAULT_RULE_DIR = "sentinel" + File.separator + "rules";

    /**
     * 默认流控规则文件名。
     */
    private static final String DEFAULT_FLOW_RULE_FILE = "FlowRule.json";

    /**
     * 默认降级规则文件名。
     */
    private static final String DEFAULT_DEGRADE_RULE_FILE = "DegradeRule.json";

    /**
     * 默认系统规则文件名。
     */
    private static final String DEFAULT_SYSTEM_RULE_FILE = "SystemRule.json";

    /**
     * 默认授权规则文件名。
     */
    private static final String DEFAULT_AUTHORITY_RULE_FILE = "AuthorityRule.json";

    /**
     * 初始化 Sentinel 文件数据源。
     * <p>
     * 从 sentinel.properties 读取配置，创建文件数据源并注册到对应的规则管理器。
     *
     * @throws Exception 初始化失败时抛出异常
     */
    @Override
    public void init() throws Exception {
        String ruleDir = System.getProperty("user.dir") + File.separator + DEFAULT_RULE_DIR;
        Props props = null;
        try {
            props = new Props("sentinel.properties");
        } catch (Exception throwable) {
            log.warn("读取 sentinel.properties 配置文件失败", throwable);
        }
        log.info("init ruleDir={}, props={}", ruleDir, JSON.toJSONString(props));
        String flowRuleFile = DEFAULT_FLOW_RULE_FILE;
        String degradeRuleFile = DEFAULT_DEGRADE_RULE_FILE;
        String systemRuleFile = DEFAULT_SYSTEM_RULE_FILE;
        String authorityRuleFile = DEFAULT_AUTHORITY_RULE_FILE;
        if (props != null) {
            ruleDir = props.getStr("com.github.fanzezhen.fun.framework.core.common.sentinel.file.dir", ruleDir);
            flowRuleFile = props.getStr("com.github.fanzezhen.fun.framework.core.common.sentinel.file.flow-rule",
                    DEFAULT_FLOW_RULE_FILE);
            degradeRuleFile = props.getStr("com.github.fanzezhen.fun.framework.core.common.sentinel.file.degrade-rule",
                    DEFAULT_DEGRADE_RULE_FILE);
            systemRuleFile = props.getStr("com.github.fanzezhen.fun.framework.core.common.sentinel.file.system-rule",
                    DEFAULT_SYSTEM_RULE_FILE);
            authorityRuleFile = props.getStr("com.github.fanzezhen.fun.framework.core.common.sentinel.file.authority-rule",
                    DEFAULT_AUTHORITY_RULE_FILE);
        }
        String flowRulePath = ruleDir + File.separator + flowRuleFile;
        String degradeRulePath = ruleDir + File.separator + degradeRuleFile;
        String systemRulePath = ruleDir + File.separator + systemRuleFile;
        String authorityRulePath = ruleDir + File.separator + authorityRuleFile;

        // 注册流控规则数据源
        ReadableDataSource<String, List<FlowRule>> ds = new FileRefreshableDataSource<>(
                flowRulePath, source -> JSON.parseObject(source, new TypeReference<>() {
        })
        );
        FlowRuleManager.register2Property(ds.getProperty());
        WritableDataSource<List<FlowRule>> wds = new FileWritableDataSource<>(flowRulePath, this::encodeJson);
        WritableDataSourceRegistry.registerFlowDataSource(wds);

        // 注册降级规则数据源
        ReadableDataSource<String, List<DegradeRule>> degradeDs = new FileRefreshableDataSource<>(
                degradeRulePath, source -> JSON.parseObject(source, new TypeReference<>() {
        })
        );
        DegradeRuleManager.register2Property(degradeDs.getProperty());
        WritableDataSource<List<DegradeRule>> degradeWritableDataSource =
                new FileWritableDataSource<>(degradeRulePath, this::encodeJson);
        WritableDataSourceRegistry.registerDegradeDataSource(degradeWritableDataSource);

        // 注册系统规则数据源
        ReadableDataSource<String, List<SystemRule>> systemDs = new FileRefreshableDataSource<>(
                systemRulePath, source -> JSON.parseObject(source, new TypeReference<>() {
        })
        );
        SystemRuleManager.register2Property(systemDs.getProperty());
        WritableDataSource<List<SystemRule>> systemWritableDataSource =
                new FileWritableDataSource<>(systemRulePath, this::encodeJson);
        WritableDataSourceRegistry.registerSystemDataSource(systemWritableDataSource);

        // 注册授权规则数据源
        ReadableDataSource<String, List<AuthorityRule>> authorityDs = new FileRefreshableDataSource<>(
                authorityRulePath, source -> JSON.parseObject(source, new TypeReference<>() {
        })
        );
        AuthorityRuleManager.register2Property(authorityDs.getProperty());
        WritableDataSource<List<AuthorityRule>> authorityWritableDataSource =
                new FileWritableDataSource<>(authorityRulePath, this::encodeJson);
        WritableDataSourceRegistry.registerAuthorityDataSource(authorityWritableDataSource);
    }

    /**
     * 将对象编码为 JSON 字符串。
     *
     * @param t 待编码的对象
     * @param <T> 对象类型
     * @return JSON 字符串
     */
    private <T> String encodeJson(final T t) {
        return JSON.toJSONString(t);
    }
}
