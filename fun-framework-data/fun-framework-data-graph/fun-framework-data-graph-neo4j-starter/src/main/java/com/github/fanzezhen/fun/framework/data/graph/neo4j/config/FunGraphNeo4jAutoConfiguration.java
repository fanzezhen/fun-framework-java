package com.github.fanzezhen.fun.framework.data.graph.neo4j.config;

import com.github.fanzezhen.fun.framework.core.log.support.FunLogHelper;
import com.github.fanzezhen.fun.framework.data.graph.base.config.FunGraphProperties;
import com.github.fanzezhen.fun.framework.data.graph.base.mapping.IGraphMapper;
import com.github.fanzezhen.fun.framework.data.graph.base.template.BaseMultiDatasourceGraphTemplate;
import com.github.fanzezhen.fun.framework.data.graph.neo4j.template.Neo4jMultiDatasourceGraphTemplate;
import org.neo4j.driver.Driver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Neo4j 图数据库自动配置类
 * <p>
 * 装配多数据源图操作模板。子项目注册自己的
 * {@link BaseMultiDatasourceGraphTemplate} bean 即可覆盖框架默认实现。
 * </p>
 *
 * @since 4.1.1
 */
@Configuration
@ConditionalOnClass(Driver.class)
@EnableConfigurationProperties(FunGraphProperties.class)
public class FunGraphNeo4jAutoConfiguration {

    /**
     * 创建 Neo4j 多数据源图操作模板
     *
     * @param funGraphProperties 图数据库配置
     * @param funLogHelper       日志辅助工具
     * @param graphMapper        结果映射引擎
     * @return 多数据源图操作模板
     */
    @Bean
    @ConditionalOnMissingBean(BaseMultiDatasourceGraphTemplate.class)
    public BaseMultiDatasourceGraphTemplate funNeo4jGraphTemplate(final FunGraphProperties funGraphProperties,
                                                                  final FunLogHelper funLogHelper,
                                                                  final IGraphMapper graphMapper) {
        return new Neo4jMultiDatasourceGraphTemplate(funGraphProperties, funLogHelper, graphMapper);
    }
}
