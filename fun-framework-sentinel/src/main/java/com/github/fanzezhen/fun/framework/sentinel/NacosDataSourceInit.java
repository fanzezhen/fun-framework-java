package com.github.fanzezhen.fun.framework.sentinel;

import cn.hutool.setting.dialect.Props;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Sentinel Nacos 数据源初始化器。
 * <p>
 * 通过 Sentinel 的 SPI 机制自动注册基于 Nacos 的规则数据源。
 * <p>
 * 使用方法：在 META-INF/services/com.alibaba.csp.sentinel.init.InitFunc 文件中添加本类全限定名。
 * <p>
 * 配置项（通过 sentinel.properties 文件）：
 * <ul>
 *   <li>com.github.fanzezhen.fun.framework.core.common.sentinel.remote-address - Nacos 服务器地址</li>
 *   <li>com.github.fanzezhen.fun.framework.core.common.sentinel.groupId - Nacos 配置分组 ID</li>
 *   <li>com.github.fanzezhen.fun.framework.core.common.sentinel.dataId - Nacos 配置数据 ID</li>
 * </ul>
 */
@Slf4j
public class NacosDataSourceInit implements InitFunc {

    /**
     * 默认 Nacos 服务器地址。
     */
    private static final String DEFAULT_REMOTE_ADDRESS = "localhost";

    /**
     * 初始化 Sentinel Nacos 数据源。
     * <p>
     * 从 sentinel.properties 读取 Nacos 配置，创建数据源并注册到流控规则管理器。
     *
     * @throws Exception 初始化失败时抛出异常
     */
    @Override
    public void init() throws Exception {
        Props props = new Props("sentinel.properties");
        String remoteAddress = props.getStr("com.github.fanzezhen.fun.framework.core.common.sentinel.remote-address",
                DEFAULT_REMOTE_ADDRESS);
        String groupId = props.getStr("com.github.fanzezhen.fun.framework.core.common.sentinel.groupId");
        String dataId = props.getStr("com.github.fanzezhen.fun.framework.core.common.sentinel.dataId");

        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(remoteAddress, groupId, dataId,
                source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
                }));
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
    }
}
