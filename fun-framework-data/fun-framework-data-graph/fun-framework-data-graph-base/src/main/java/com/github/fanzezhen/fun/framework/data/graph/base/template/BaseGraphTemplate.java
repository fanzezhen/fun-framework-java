package com.github.fanzezhen.fun.framework.data.graph.base.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.fanzezhen.fun.framework.core.log.support.FunLogHelper;
import com.github.fanzezhen.fun.framework.core.model.common.FunFunction;
import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;
import com.github.fanzezhen.fun.framework.core.model.enums.FunCoreDataExceptionEnum;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.data.graph.base.config.FunGraphProperties;
import com.github.fanzezhen.fun.framework.data.graph.base.enums.FunDataGraphExceptionEnum;
import com.github.fanzezhen.fun.framework.data.graph.base.mapping.GraphClassCache;
import com.github.fanzezhen.fun.framework.data.graph.base.mapping.GraphEntityMeta;
import com.github.fanzezhen.fun.framework.data.graph.base.mapping.IGraphMapper;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRecordData;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 图数据库操作模板抽象基类
 * <p>
 * 把全部对外能力收敛到三个驱动原语上：{@link #doQuery}、{@link #doExecute}、
 * {@link #queryNative}。新增图数据库实现只需实现这三个方法，
 * CRUD 语义、Cypher 生成、结果映射、写入回填都在本类完成。
 * </p>
 *
 * @since 4.1.1
 */
public abstract class BaseGraphTemplate implements IGraphTemplate {

    /**
     * 数据源配置
     */
    protected final FunGraphProperties.Config config;

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
     * @param config       数据源配置
     * @param funLogHelper 日志辅助工具
     * @param graphMapper  结果映射引擎
     */
    protected BaseGraphTemplate(final FunGraphProperties.Config config,
                                final FunLogHelper funLogHelper,
                                final IGraphMapper graphMapper) {
        this.config = config;
        this.funLogHelper = funLogHelper;
        this.graphMapper = graphMapper;
    }

    /**
     * 执行查询语句并把结果归一化为中间表示
     *
     * @param cypher Cypher 查询语句
     * @param params 绑定参数
     * @return 结果行列表
     */
    protected abstract List<GraphRecordData> doQuery(String cypher, Map<String, Object> params);

    /**
     * 执行写语句
     *
     * @param cypher Cypher 写语句
     * @param params 绑定参数
     * @return 受影响的记录数
     */
    protected abstract long doExecute(String cypher, Map<String, Object> params);

    @Override
    public List<GraphRecordData> queryRecordList(final String cypher, final Map<String, Object> params) {
        return executeByLog(cypher, statement -> doQuery(statement, safeParams(params)));
    }

    @Override
    public long execute(final String cypher, final Map<String, Object> params) {
        return executeByLog(cypher, statement -> doExecute(statement, safeParams(params)));
    }

    @Override
    public <T> List<T> queryList(final String cypher, final Map<String, Object> params, final Class<T> clz) {
        return graphMapper.mapList(queryRecordList(cypher, params), clz);
    }

    @Override
    public <T> T queryOne(final String cypher, final Map<String, Object> params, final Class<T> clz) {
        final List<GraphRecordData> recordList = queryRecordList(cypher, params);
        return CollUtil.isEmpty(recordList) ? null : graphMapper.mapOne(recordList.get(0), clz);
    }

    @Override
    public <T> T queryObject(final String cypher, final Map<String, Object> params, final Class<T> clz) {
        final List<GraphRecordData> recordList = queryRecordList(cypher, params);
        return CollUtil.isEmpty(recordList) ? null : graphMapper.mapObject(recordList.get(0), clz);
    }

    @Override
    public <T> T get(final String column, final Serializable value, final Class<T> clz) {
        final String cypher = CypherBuilder.matchByProperty(requireLabel(clz), column, CypherBuilder.PARAM_VALUE);
        return queryOne(cypher, singleParam(CypherBuilder.PARAM_VALUE, value), clz);
    }

    @Override
    public <T> T getById(final Serializable id, final Class<T> clz) {
        final String cypher = CypherBuilder.matchByElementId(requireLabel(clz), CypherBuilder.PARAM_ID);
        return queryOne(cypher, singleParam(CypherBuilder.PARAM_ID, id), clz);
    }

    @Override
    public <T> List<T> listByIds(final Collection<? extends Serializable> ids, final Class<T> clz) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        final String cypher = CypherBuilder.matchByElementIdIn(requireLabel(clz), CypherBuilder.PARAM_IDS);
        return queryList(cypher, singleParam(CypherBuilder.PARAM_IDS, new ArrayList<>(ids)), clz);
    }

    @Override
    public <T> List<T> listByColumn(final String column, final Serializable value, final Class<T> clz) {
        final String cypher = CypherBuilder.matchByProperty(requireLabel(clz), column, CypherBuilder.PARAM_VALUE);
        return queryList(cypher, singleParam(CypherBuilder.PARAM_VALUE, value), clz);
    }

    @Override
    public <T> List<T> listByColumn(final String column,
                                    final Collection<? extends Serializable> values,
                                    final Class<T> clz) {
        if (CollUtil.isEmpty(values)) {
            return Collections.emptyList();
        }
        final String cypher = CypherBuilder.matchByPropertyIn(requireLabel(clz), column, CypherBuilder.PARAM_VALUES);
        return queryList(cypher, singleParam(CypherBuilder.PARAM_VALUES, new ArrayList<>(values)), clz);
    }

    @Override
    public boolean insert(final Collection<IEntity<String>> entities) {
        return writeByGroup(entities, false);
    }

    @Override
    public boolean merge(final Collection<? extends IEntity<String>> entities) {
        return writeByGroup(entities, true);
    }

    @Override
    public boolean deleteById(final Collection<String> ids, final Class<? extends IEntity<String>> clz) {
        if (CollUtil.isEmpty(ids)) {
            return true;
        }
        final GraphEntityMeta meta = requireGraphMeta(clz);
        final List<String> idList = new ArrayList<>(ids);
        final String cypher = meta.isRelationshipEntity()
            ? CypherBuilder.deleteRelationshipByElementIdIn(meta.getLabel(), CypherBuilder.PARAM_IDS)
            : CypherBuilder.detachDeleteByElementIdIn(meta.getLabel(), CypherBuilder.PARAM_IDS);
        return execute(cypher, singleParam(CypherBuilder.PARAM_IDS, idList)) > 0;
    }

    /**
     * 按实体类型分组写入
     * <p>
     * 允许集合内混杂多种实体类型，按类型分组后各自生成语句，
     * 免去调用方为"同类型"约束做额外拆分。
     * </p>
     *
     * @param entities   实体集合
     * @param mergeMode  true 表示按业务主键合并，false 表示纯新增
     * @return true 表示写入成功
     */
    private boolean writeByGroup(final Collection<? extends IEntity<String>> entities, final boolean mergeMode) {
        if (CollUtil.isEmpty(entities)) {
            return true;
        }
        final Map<Class<?>, List<IEntity<String>>> groupedMap = new LinkedHashMap<>();
        int expectedCount = 0;
        for (final IEntity<String> entity : entities) {
            if (entity != null) {
                groupedMap.computeIfAbsent(entity.getClass(), key -> new ArrayList<>()).add(entity);
                expectedCount++;
            }
        }
        int writtenCount = 0;
        for (final Map.Entry<Class<?>, List<IEntity<String>>> entry : groupedMap.entrySet()) {
            writtenCount += writeSameClass(entry.getKey(), entry.getValue(), mergeMode);
        }
        return writtenCount == expectedCount;
    }

    /**
     * 写入同一类型的实体集合
     *
     * @param entityClass 实体类型
     * @param entityList  实体列表
     * @param mergeMode   true 表示按业务主键合并
     * @return 实际写入的记录数
     */
    private int writeSameClass(final Class<?> entityClass,
                               final List<IEntity<String>> entityList,
                               final boolean mergeMode) {
        final GraphEntityMeta meta = requireGraphMeta(entityClass);
        if (meta.isRelationshipEntity()) {
            return writeRelationships(meta, entityList);
        }
        final List<Map<String, Object>> rowList = new ArrayList<>(entityList.size());
        for (final IEntity<String> entity : entityList) {
            rowList.add(extractProperties(meta, entity));
        }
        final String cypher = mergeMode
            ? CypherBuilder.mergeNodes(meta.getLabel(), requireBusinessProperty(meta))
            : CypherBuilder.createNodes(meta.getLabel());
        final List<GraphRecordData> recordList =
            queryRecordList(cypher, singleParam(CypherBuilder.PARAM_ROWS, rowList));
        writeBackElementIds(meta, entityList, recordList);
        return recordList == null ? 0 : recordList.size();
    }

    /**
     * 写入关系实体集合
     * <p>
     * 关系两端必须已持有图库标识，否则无法定位节点，该行被跳过。跳过的行不计入返回数，
     * 使调用方能从 {@code insert} 的返回值察觉"部分关系未写入"，而非静默丢失。
     * </p>
     *
     * @param meta       关系实体元数据
     * @param entityList 关系实体列表
     * @return 实际写入的记录数
     */
    private int writeRelationships(final GraphEntityMeta meta, final List<IEntity<String>> entityList) {
        final List<Map<String, Object>> rowList = new ArrayList<>(entityList.size());
        final List<IEntity<String>> writableList = new ArrayList<>(entityList.size());
        for (final IEntity<String> entity : entityList) {
            final String startId = endpointElementId(meta.getStartNodeField(), entity);
            final String endId = endpointElementId(meta.getEndNodeField(), entity);
            if (CharSequenceUtil.isNotEmpty(startId) && CharSequenceUtil.isNotEmpty(endId)) {
                rowList.add(Map.of(
                    CypherBuilder.ROW_START_ID, startId,
                    CypherBuilder.ROW_END_ID, endId,
                    CypherBuilder.ROW_PROPERTIES, extractProperties(meta, entity)));
                writableList.add(entity);
            }
        }
        if (rowList.isEmpty()) {
            return 0;
        }
        final String cypher = CypherBuilder.createRelationships(meta.getLabel());
        final List<GraphRecordData> recordList =
            queryRecordList(cypher, singleParam(CypherBuilder.PARAM_ROWS, rowList));
        writeBackElementIds(meta, writableList, recordList);
        return recordList == null ? 0 : recordList.size();
    }

    /**
     * 取关系端点节点的图库标识
     *
     * @param endpointField 端点字段
     * @param entity        关系实体
     * @return 端点节点的图库标识，无法获取返回 null
     */
    private static String endpointElementId(final Field endpointField, final Object entity) {
        if (endpointField == null) {
            return null;
        }
        final Object endpoint = ReflectUtil.getFieldValue(entity, endpointField);
        if (endpoint == null) {
            return null;
        }
        final Field elementIdField = GraphClassCache.getMeta(endpoint.getClass()).getElementIdField();
        if (elementIdField == null) {
            return null;
        }
        final Object elementId = ReflectUtil.getFieldValue(endpoint, elementIdField);
        return elementId == null ? null : elementId.toString();
    }

    /**
     * 提取实体的可写属性
     * <p>
     * 值为 null 的属性不写入，避免把"未设置"表达成"清空属性"。
     * </p>
     *
     * @param meta   实体元数据
     * @param entity 实体
     * @return 属性映射
     */
    private static Map<String, Object> extractProperties(final GraphEntityMeta meta, final Object entity) {
        final Map<String, Object> propertyMap = new LinkedHashMap<>();
        meta.getWritablePropertyMap().forEach((propertyName, field) -> {
            final Object value = ReflectUtil.getFieldValue(entity, field);
            if (value != null) {
                propertyMap.put(propertyName, value);
            }
        });
        return propertyMap;
    }

    /**
     * 把图库生成的标识回写到实体
     * <p>
     * 写入语句按行返回标识，顺序与入参一致，据此逐个回填，
     * 让 {@code insert} 后的实体立刻可用于后续按标识的操作。
     * </p>
     *
     * @param meta       实体元数据
     * @param entityList 实体列表
     * @param recordList 写入结果行列表
     */
    private static void writeBackElementIds(final GraphEntityMeta meta,
                                            final List<IEntity<String>> entityList,
                                            final List<GraphRecordData> recordList) {
        final Field elementIdField = meta.getElementIdField();
        if (elementIdField == null || CollUtil.isEmpty(recordList)) {
            return;
        }
        final int size = Math.min(entityList.size(), recordList.size());
        for (int index = 0; index < size; index++) {
            final Object elementId = recordList.get(index).firstValue();
            if (elementId != null) {
                ReflectUtil.setFieldValue(entityList.get(index), elementIdField, elementId.toString());
            }
        }
    }

    /**
     * 取实体的图标签或关系类型
     *
     * @param clz 实体类型
     * @return 标签或关系类型
     * @throws ServiceException 实体未标注图注解时抛出
     */
    protected static String requireLabel(final Class<?> clz) {
        return requireGraphMeta(clz).getLabel();
    }

    /**
     * 取实体的图元数据并校验已标注图注解
     *
     * @param clz 实体类型
     * @return 图元数据
     * @throws ServiceException 实体未标注图注解时抛出
     */
    protected static GraphEntityMeta requireGraphMeta(final Class<?> clz) {
        final GraphEntityMeta meta = GraphClassCache.getMeta(clz);
        if (!meta.isGraphEntity() || CharSequenceUtil.isEmpty(meta.getLabel())) {
            throw new ServiceException(FunDataGraphExceptionEnum.GRAPH_ENTITY_ANNOTATION_MISSING, clz.getName());
        }
        return meta;
    }

    /**
     * 取实体的业务主键属性名
     *
     * @param meta 实体元数据
     * @return 业务主键属性名
     * @throws ServiceException 未声明业务主键时抛出
     */
    private static String requireBusinessProperty(final GraphEntityMeta meta) {
        if (CharSequenceUtil.isEmpty(meta.getBusinessIdProperty())) {
            throw new ServiceException(FunCoreDataExceptionEnum.PRIMARY_KEY_MISSING,
                meta.getEntityClass().getName());
        }
        return meta.getBusinessIdProperty();
    }

    /**
     * 参数归一化，避免驱动收到 null 参数映射
     *
     * @param params 原始参数
     * @return 参数映射，null 时返回空映射
     */
    protected static Map<String, Object> safeParams(final Map<String, Object> params) {
        return params == null ? Collections.emptyMap() : params;
    }

    /**
     * 构造单参数映射
     * <p>
     * 不用 {@code Map.of}：它拒绝 null 值，会让"按 null 条件查询"这种调用方失误
     * 变成一个没有上下文的 NPE。允许 null 后，Cypher 中 {@code = null} 永不匹配，
     * 结果自然是"查不到"，边界情况并入常规逻辑。
     * </p>
     *
     * @param name  参数名
     * @param value 参数值，允许为 null
     * @return 参数映射
     */
    protected static Map<String, Object> singleParam(final String name, final Object value) {
        final Map<String, Object> paramMap = LinkedHashMap.newLinkedHashMap(1);
        paramMap.put(name, value);
        return paramMap;
    }

    /**
     * 带日志执行
     *
     * @param requestParam 请求参数（Cypher 语句）
     * @param invoker      执行器
     * @param <R>          返回类型
     * @return 执行结果
     */
    protected <R> R executeByLog(final String requestParam, final FunFunction<String, R> invoker) {
        return funLogHelper.executeByLog(this.getClass().getName(), invoker, requestParam);
    }
}
