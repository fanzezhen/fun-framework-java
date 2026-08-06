package com.github.fanzezhen.fun.framework.data.graph.base.template;

import com.github.fanzezhen.fun.framework.core.log.support.FunLogHelper;
import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;
import com.github.fanzezhen.fun.framework.core.model.template.BaseMultiDatasourceTemplate;
import com.github.fanzezhen.fun.framework.data.graph.base.config.FunGraphProperties;
import com.github.fanzezhen.fun.framework.data.graph.base.mapping.IGraphMapper;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRecordData;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 图数据库多数据源操作模板抽象基类
 * <p>
 * 数据源索引、按 {@code @Entity(datasource)} 路由、默认数据源回退、子模板惰性创建
 * 等与存储无关的能力由 {@link BaseMultiDatasourceTemplate} 承担，本类只负责把
 * {@link IGraphTemplate} 的各方法委派到路由出的子模板。
 * </p>
 * <p>
 * 不涉及实体类型的方法（直接执行 Cypher）无法从实体推断数据源，统一走默认数据源；
 * 需要指定数据源时用 {@link #findTemplate(String)} 取具体模板。
 * </p>
 *
 * @since 4.1.1
 */
public abstract class BaseMultiDatasourceGraphTemplate
    extends BaseMultiDatasourceTemplate<IGraphTemplate, FunGraphProperties.Config>
    implements IGraphTemplate {

    /**
     * 图数据库配置
     */
    protected final FunGraphProperties funGraphProperties;

    /**
     * 日志辅助工具
     */
    protected final FunLogHelper funLogHelper;

    /**
     * 结果映射引擎
     */
    protected final IGraphMapper graphMapper;

    /**
     * 构造方法
     *
     * @param funGraphProperties 图数据库配置
     * @param funLogHelper       日志辅助工具
     * @param graphMapper        结果映射引擎
     */
    protected BaseMultiDatasourceGraphTemplate(final FunGraphProperties funGraphProperties,
                                               final FunLogHelper funLogHelper,
                                               final IGraphMapper graphMapper) {
        super(funGraphProperties == null ? null : funGraphProperties.getDefaultDatasource(),
            funGraphProperties == null ? null : funGraphProperties.getConfigs(),
            GRAPH_MARK);
        this.funGraphProperties = funGraphProperties;
        this.funLogHelper = funLogHelper;
        this.graphMapper = graphMapper;
    }

    /**
     * 按实体集合路由子模板
     * <p>
     * 取首个非空实体的类型做路由，集合内实体应属同一数据源。
     * </p>
     *
     * @param entities 实体集合
     * @return 子模板实例
     */
    protected IGraphTemplate findTemplate(final Collection<? extends IEntity<String>> entities) {
        if (entities == null) {
            return getDefaultOrAnyTemplate();
        }
        for (final IEntity<String> entity : entities) {
            if (entity != null) {
                return findTemplate(entity.getClass());
            }
        }
        return getDefaultOrAnyTemplate();
    }

    @Override
    public <T> List<T> queryList(final String cypher, final Map<String, Object> params, final Class<T> clz) {
        return findTemplate(clz).queryList(cypher, params, clz);
    }

    @Override
    public <T> T queryOne(final String cypher, final Map<String, Object> params, final Class<T> clz) {
        return findTemplate(clz).queryOne(cypher, params, clz);
    }

    @Override
    public <T> T queryObject(final String cypher, final Map<String, Object> params, final Class<T> clz) {
        return findTemplate(clz).queryObject(cypher, params, clz);
    }

    @Override
    public List<GraphRecordData> queryRecordList(final String cypher, final Map<String, Object> params) {
        return getDefaultOrAnyTemplate().queryRecordList(cypher, params);
    }

    @Override
    public long execute(final String cypher, final Map<String, Object> params) {
        return getDefaultOrAnyTemplate().execute(cypher, params);
    }

    @Override
    public <R> R queryNative(final String cypher,
                             final Map<String, Object> params,
                             final Function<Object, R> resultHandler) {
        return getDefaultOrAnyTemplate().queryNative(cypher, params, resultHandler);
    }

    @Override
    public <T> T get(final String column, final Serializable value, final Class<T> clz) {
        return findTemplate(clz).get(column, value, clz);
    }

    @Override
    public <T> T getById(final Serializable id, final Class<T> clz) {
        return findTemplate(clz).getById(id, clz);
    }

    @Override
    public <T> List<T> listByIds(final Collection<? extends Serializable> ids, final Class<T> clz) {
        return findTemplate(clz).listByIds(ids, clz);
    }

    @Override
    public <T> List<T> listByColumn(final String column, final Serializable value, final Class<T> clz) {
        return findTemplate(clz).listByColumn(column, value, clz);
    }

    @Override
    public <T> List<T> listByColumn(final String column,
                                    final Collection<? extends Serializable> values,
                                    final Class<T> clz) {
        return findTemplate(clz).listByColumn(column, values, clz);
    }

    @Override
    public boolean insert(final Collection<IEntity<String>> entities) {
        return findTemplate(entities).insert(entities);
    }

    @Override
    public boolean merge(final Collection<? extends IEntity<String>> entities) {
        return findTemplate(entities).merge(entities);
    }

    @Override
    public boolean deleteById(final Collection<String> ids, final Class<? extends IEntity<String>> clz) {
        return findTemplate(clz).deleteById(ids, clz);
    }
}
