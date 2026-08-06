package com.github.fanzezhen.fun.framework.data.graph.neo4j.template;

import com.github.fanzezhen.fun.framework.core.log.support.FunLogHelper;
import com.github.fanzezhen.fun.framework.data.graph.base.config.FunGraphProperties;
import com.github.fanzezhen.fun.framework.data.graph.base.mapping.IGraphMapper;
import com.github.fanzezhen.fun.framework.data.graph.base.template.BaseMultiDatasourceGraphTemplate;
import com.github.fanzezhen.fun.framework.data.graph.base.template.IGraphTemplate;
import com.github.fanzezhen.fun.framework.data.graph.neo4j.config.Neo4jDriverFactory;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.DisposableBean;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Neo4j 多数据源图操作模板
 * <p>
 * 每个数据源持有独立驱动与模板，模板惰性创建。驱动内部维护连接池与后台线程，
 * 容器销毁时必须关闭，否则资源不会释放。
 * </p>
 *
 * @since 4.1.1
 */
@Slf4j
public class Neo4jMultiDatasourceGraphTemplate extends BaseMultiDatasourceGraphTemplate implements DisposableBean {

    /**
     * 已创建的驱动列表
     * <p>
     * 模板惰性创建，创建动作可能发生在任意业务线程，故用并发安全的列表。
     * </p>
     */
    private final List<Driver> driverList = new CopyOnWriteArrayList<>();

    /**
     * 构造方法
     *
     * @param funGraphProperties 图数据库配置
     * @param funLogHelper       日志辅助工具
     * @param graphMapper        结果映射引擎
     */
    public Neo4jMultiDatasourceGraphTemplate(final FunGraphProperties funGraphProperties,
                                             final FunLogHelper funLogHelper,
                                             final IGraphMapper graphMapper) {
        super(funGraphProperties, funLogHelper, graphMapper);
    }

    @Override
    protected IGraphTemplate createTemplate(final FunGraphProperties.Config config) {
        final Driver driver = Neo4jDriverFactory.createDriver(config);
        driverList.add(driver);
        log.debug("创建 Neo4j 数据源模板：{}", config.getName());
        return new Neo4jGraphTemplate(driver, config, funLogHelper, graphMapper);
    }

    @Override
    public void destroy() {
        for (final Driver driver : driverList) {
            try {
                driver.close();
            } catch (final Exception exception) {
                log.warn("关闭 Neo4j 驱动失败", exception);
            }
        }
        driverList.clear();
    }
}
