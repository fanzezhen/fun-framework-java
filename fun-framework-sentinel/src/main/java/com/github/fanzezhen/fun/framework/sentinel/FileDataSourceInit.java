/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
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
public class FileDataSourceInit implements InitFunc {

    @Override
    public void init() throws Exception {
        // A fake path.
        String ruleDir = System.getProperty("user.dir") + File.separator + "sentinel" + File.separator + "rules";
        Props props = null;
        try {
            props = new Props("sentinel.properties");
        } catch (Exception throwable) {
            log.warn("", throwable);
        }
        log.info("init ruleDir={}, props={}", ruleDir, JSON.toJSONString(props));
        String flowRuleFile = "FlowRule.json";
        String degradeRuleFile = "DegradeRule.json";
        String systemRuleFile = "SystemRule.json";
        String authorityRuleFile = "AuthorityRule.json";
        if (props != null) {
            ruleDir = props.getStr("com.github.fanzezhen.fun.framework.core.common.sentinel.file.dir", ruleDir);
            flowRuleFile = props.getStr("com.github.fanzezhen.fun.framework.core.common.sentinel.file.flow-rule", "FlowRule.json");
            degradeRuleFile = props.getStr("com.github.fanzezhen.fun.framework.core.common.sentinel.file.degrade-rule", "DegradeRule.json");
            systemRuleFile = props.getStr("com.github.fanzezhen.fun.framework.core.common.sentinel.file.system-rule", "SystemRule.json");
            authorityRuleFile = props.getStr("com.github.fanzezhen.fun.framework.core.common.sentinel.file.authority-rule", "AuthorityRule.json");
        }
        String flowRulePath = ruleDir + File.separator + flowRuleFile;
        String degradeRulePath = ruleDir + File.separator + degradeRuleFile;
        String systemRulePath = ruleDir + File.separator + systemRuleFile;
        String authorityRulePath = ruleDir + File.separator + authorityRuleFile;

        ReadableDataSource<String, List<FlowRule>> ds = new FileRefreshableDataSource<>(
                flowRulePath, source -> JSON.parseObject(source, new TypeReference<>() {
        })
        );
        // Register to flow rule manager.
        FlowRuleManager.register2Property(ds.getProperty());
        WritableDataSource<List<FlowRule>> wds = new FileWritableDataSource<>(flowRulePath, this::encodeJson);
        // Register to writable data source registry so that rules can be updated to file
        // when there are rules pushed from the Sentinel Dashboard.
        WritableDataSourceRegistry.registerFlowDataSource(wds);

        ReadableDataSource<String, List<DegradeRule>> degradeDs = new FileRefreshableDataSource<>(
                degradeRulePath, source -> JSON.parseObject(source, new TypeReference<>() {
        })
        );
        // Register to degrade rule manager.
        DegradeRuleManager.register2Property(degradeDs.getProperty());
        WritableDataSource<List<DegradeRule>> degradeWritableDataSource = new FileWritableDataSource<>(degradeRulePath, this::encodeJson);
        // Register to writable data source registry so that rules can be updated to file
        // when there are rules pushed from the Sentinel Dashboard.
        WritableDataSourceRegistry.registerDegradeDataSource(degradeWritableDataSource);

        ReadableDataSource<String, List<SystemRule>> systemDs = new FileRefreshableDataSource<>(
                systemRulePath, source -> JSON.parseObject(source, new TypeReference<>() {
        })
        );
        // Register to system rule manager.
        SystemRuleManager.register2Property(systemDs.getProperty());
        WritableDataSource<List<SystemRule>> systemWritableDataSource = new FileWritableDataSource<>(systemRulePath, this::encodeJson);
        // Register to writable data source registry so that rules can be updated to file
        // when there are rules pushed from the Sentinel Dashboard.
        WritableDataSourceRegistry.registerSystemDataSource(systemWritableDataSource);

        ReadableDataSource<String, List<AuthorityRule>> authorityDs = new FileRefreshableDataSource<>(
                authorityRulePath, source -> JSON.parseObject(source, new TypeReference<>() {
        })
        );
        // Register to authority rule manager.
        AuthorityRuleManager.register2Property(authorityDs.getProperty());
        WritableDataSource<List<AuthorityRule>> authorityWritableDataSource = new FileWritableDataSource<>(authorityRulePath, this::encodeJson);
        // Register to writable data source registry so that rules can be updated to file
        // when there are rules pushed from the Sentinel Dashboard.
        WritableDataSourceRegistry.registerAuthorityDataSource(authorityWritableDataSource);
    }

    private <T> String encodeJson(T t) {
        return JSON.toJSONString(t);
    }
}
