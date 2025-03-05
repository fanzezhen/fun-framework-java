package com.github.fanzezhen.fun.framework.mp.config.properties;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author fanzezhen
 * @since 3.4.3.1
 */
@Slf4j
@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "mybatis-plus")
public class MybatisProperties {
    private GlobalConfig globalConfig;

    public TableInfo getTableInfo(String tableName) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(tableName);
        if (tableInfo == null) {
            String tablePrefix = getTablePrefix();
            tableInfo = TableInfoHelper.getTableInfo(tablePrefix + tableName);
        }
        return tableInfo;
    }

    public String getTablePrefix() {
        if (globalConfig != null) {
            GlobalConfig.DbConfig dbConfig = globalConfig.getDbConfig();
            if (dbConfig != null) {
                return dbConfig.getTablePrefix();
            }
        }
        return null;
    }
}
