package com.github.fanzezhen.fun.framework.sentinel;

import cn.hutool.setting.dialect.Props;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <p>
 * A sample showing how to register readable and writable data source via Sentinel init SPI mechanism.
 * </p>
 * <p>
 * To activate this, you can add the class name to `com.alibaba.csp.sentinel.init.InitFunc` file
 * in `META-INF/services/` directory of the resource directory. Then the data source will be automatically
 * registered during the initialization of Sentinel.
 * </p>
 *
 * @author Eric Zhao
 */
@Slf4j
public class NacosDataSourceInit implements InitFunc {

    @Override
    public void init() throws Exception {
        Props props = new Props("sentinel.properties");
        String remoteAddress = props.getStr("com.github.fanzezhen.fun.framework.core.common.sentinel.remote-address", "localhost");
        String groupId = props.getStr("com.github.fanzezhen.fun.framework.core.common.sentinel.groupId");
        String dataId = props.getStr("com.github.fanzezhen.fun.framework.core.common.sentinel.dataId");

        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(remoteAddress, groupId, dataId,
                source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {}));
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
    }
}
