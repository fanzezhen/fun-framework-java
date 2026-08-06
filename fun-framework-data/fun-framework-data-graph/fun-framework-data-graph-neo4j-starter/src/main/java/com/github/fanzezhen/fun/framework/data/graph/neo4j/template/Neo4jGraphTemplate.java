package com.github.fanzezhen.fun.framework.data.graph.neo4j.template;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.log.support.FunLogHelper;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.data.graph.base.config.FunGraphProperties;
import com.github.fanzezhen.fun.framework.data.graph.base.enums.FunDataGraphExceptionEnum;
import com.github.fanzezhen.fun.framework.data.graph.base.mapping.IGraphMapper;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRecordData;
import com.github.fanzezhen.fun.framework.data.graph.base.template.BaseGraphTemplate;
import com.github.fanzezhen.fun.framework.data.graph.neo4j.convert.Neo4jRecordConverter;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.summary.SummaryCounters;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Neo4j 图操作模板
 * <p>
 * 基于 {@code neo4j-java-driver} 实现三个驱动原语：查询、写入、原生结果访问。
 * CRUD 语义、Cypher 生成与结果映射由 {@link BaseGraphTemplate} 承担。
 * </p>
 * <p>
 * 每次操作独立开启会话并在结束时关闭，连接复用由驱动内置的连接池负责，
 * 因此本类无状态且线程安全。
 * </p>
 *
 * @since 4.1.1
 */
@Slf4j
public class Neo4jGraphTemplate extends BaseGraphTemplate {

    /**
     * Neo4j 驱动标识，用于异常信息
     */
    private static final String DRIVER_MARK = "Neo4j";

    /**
     * Neo4j 驱动
     */
    private final Driver driver;

    /**
     * 会话配置
     */
    private final SessionConfig sessionConfig;

    /**
     * 构造方法
     *
     * @param driver       Neo4j 驱动
     * @param config       数据源配置
     * @param funLogHelper 日志辅助工具
     * @param graphMapper  结果映射引擎
     */
    public Neo4jGraphTemplate(final Driver driver,
                              final FunGraphProperties.Config config,
                              final FunLogHelper funLogHelper,
                              final IGraphMapper graphMapper) {
        super(config, funLogHelper, graphMapper);
        this.driver = driver;
        this.sessionConfig = buildSessionConfig(config);
    }

    @Override
    protected List<GraphRecordData> doQuery(final String cypher, final Map<String, Object> params) {
        try (Session session = openSession()) {
            final Result result = session.run(cypher, params);
            return Neo4jRecordConverter.toRecordDataList(result.list());
        } catch (final Exception exception) {
            throw wrap(exception, cypher);
        }
    }

    @Override
    protected long doExecute(final String cypher, final Map<String, Object> params) {
        try (Session session = openSession()) {
            final SummaryCounters counters = session.run(cypher, params).consume().counters();
            return countAffected(counters);
        } catch (final Exception exception) {
            throw wrap(exception, cypher);
        }
    }

    @Override
    public <R> R queryNative(final String cypher,
                             final Map<String, Object> params,
                             final Function<Object, R> resultHandler) {
        return executeByLog(cypher, statement -> {
            try (Session session = openSession()) {
                return resultHandler.apply(session.run(statement, safeParams(params)));
            } catch (final Exception exception) {
                throw wrap(exception, statement);
            }
        });
    }

    /**
     * 开启会话
     *
     * @return 会话
     */
    private Session openSession() {
        return sessionConfig == null ? driver.session() : driver.session(sessionConfig);
    }

    /**
     * 汇总受影响记录数
     * <p>
     * 图写入的"受影响"分散在节点、关系、属性、标签四类计数中，
     * 取其和作为统一口径，让调用方无需了解 Neo4j 的计数细分。
     * </p>
     *
     * @param counters 驱动计数器
     * @return 受影响记录数
     */
    private static long countAffected(final SummaryCounters counters) {
        if (counters == null) {
            return 0L;
        }
        return (long) counters.nodesCreated()
            + counters.nodesDeleted()
            + counters.relationshipsCreated()
            + counters.relationshipsDeleted()
            + counters.propertiesSet()
            + counters.labelsAdded()
            + counters.labelsRemoved();
    }

    /**
     * 构建会话配置
     *
     * @param config 数据源配置
     * @return 会话配置，未指定数据库时返回 null 以走驱动默认库
     */
    private static SessionConfig buildSessionConfig(final FunGraphProperties.Config config) {
        if (config == null || CharSequenceUtil.isEmpty(config.getDatabase())) {
            return null;
        }
        return SessionConfig.forDatabase(config.getDatabase());
    }

    /**
     * 包装驱动异常为框架业务异常
     * <p>
     * 已是框架业务异常则原样抛出，避免嵌套包装丢失原始错误码。
     * </p>
     *
     * @param exception 原始异常
     * @param cypher    执行的语句
     * @return 业务异常
     */
    private static ServiceException wrap(final Exception exception, final String cypher) {
        if (exception instanceof ServiceException serviceException) {
            return serviceException;
        }
        log.warn("Neo4j 执行失败：{}", cypher, exception);
        return new ServiceException(exception, FunDataGraphExceptionEnum.QUERY_EXECUTE_FAILED,
            DRIVER_MARK, exception.getLocalizedMessage());
    }
}
