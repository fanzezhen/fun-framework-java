package com.github.fanzezhen.fun.framework.data.graph.base.template;

import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;
import com.github.fanzezhen.fun.framework.core.model.template.ITemplate;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRecordData;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 图数据库操作模板接口
 * <p>
 * 原理：Cypher 语句携带参数 → 参数绑定执行 → 结果归一化为 {@link GraphRecordData}
 * → 映射引擎转为业务对象。
 * </p>
 * <p>
 * 继承 {@link ITemplate} 获得与框架其他数据模块一致的 CRUD 语义，主键类型为
 * {@link String}，对应图库内部标识（Neo4j 的 elementId）；同时提供图专属的
 * Cypher 查询方法。
 * </p>
 *
 * @since 4.1.1
 */
public interface IGraphTemplate extends ITemplate<String> {

    /**
     * 图数据库标识，用于异常信息与日志区分数据源类型
     */
    String GRAPH_MARK = "（graph）";

    /**
     * 查询多条记录并映射为对象列表
     * <p>
     * 目标类型为图实体时直接映射结果中的节点/关系列，否则按结果列名映射到同名字段。
     * </p>
     *
     * @param cypher Cypher 查询语句
     * @param params 绑定参数，可为 null
     * @param clz    返回值类型
     * @param <T>    返回值类型泛型
     * @return 查询结果列表，无结果返回空列表
     */
    <T> List<T> queryList(String cypher, Map<String, Object> params, Class<T> clz);

    /**
     * 查询单条记录并映射为对象
     * <p>
     * 取结果的第一行，多行时其余行被忽略。
     * </p>
     *
     * @param cypher Cypher 查询语句
     * @param params 绑定参数，可为 null
     * @param clz    返回值类型
     * @param <T>    返回值类型泛型
     * @return 查询结果，无结果返回 null
     */
    <T> T queryOne(String cypher, Map<String, Object> params, Class<T> clz);

    /**
     * 查询单行单列值
     * <p>
     * 用于 {@code RETURN count(n)}、{@code RETURN n.name} 这类单值查询，
     * 与 {@link #queryOne} 的区别是取首行首列而非整行映射。
     * </p>
     *
     * @param cypher Cypher 查询语句
     * @param params 绑定参数，可为 null
     * @param clz    返回值类型
     * @param <T>    返回值类型泛型
     * @return 查询结果，无结果返回 null
     */
    <T> T queryObject(String cypher, Map<String, Object> params, Class<T> clz);

    /**
     * 查询归一化结果行
     * <p>
     * 返回驱动无关的中间表示，适用于结果结构不固定、需要自行处理的场景，
     * 既避免定义一次性的映射类型，也不会像原生结果那样绑定具体图数据库。
     * </p>
     *
     * @param cypher Cypher 查询语句
     * @param params 绑定参数，可为 null
     * @return 结果行列表，无结果返回空列表
     */
    List<GraphRecordData> queryRecordList(String cypher, Map<String, Object> params);

    /**
     * 执行写语句
     *
     * @param cypher Cypher 写语句
     * @param params 绑定参数，可为 null
     * @return 受影响的记录数（节点、关系、属性变更计数之和）
     */
    long execute(String cypher, Map<String, Object> params);

    /**
     * 批量新增节点或关系
     * <p>
     * 写入成功后把图库生成的标识回写到实体的 {@code @GraphId} 字段。
     * 集合内可混杂多种实体类型，按类型分组后各自写入。
     * </p>
     * <p>
     * 写入关系要求两端节点已持有图库标识（先写节点再写关系），未满足的关系无法定位两端而被跳过，
     * 此时返回 false —— 据此可察觉"部分关系未写入"，不必逐个检查实体标识是否回填。
     * </p>
     *
     * @param entities 实体集合
     * @return 全部实体都已写入返回 true；有实体被跳过返回 false（执行出错会抛异常而非返回 false）
     */
    @Override
    boolean insert(Collection<IEntity<String>> entities);

    /**
     * 按图库标识删除节点或关系
     * <p>
     * 节点走 {@code DETACH DELETE}，连带删除其关系；关系只删关系本身，两端节点保留。
     * </p>
     *
     * @param ids 图库标识集合
     * @param clz 实体类型
     * @return 有记录被实际删除返回 true；标识均未匹配到记录返回 false。
     *         入参为空集合视为无事可做，返回 true
     */
    @Override
    boolean deleteById(Collection<String> ids, Class<? extends IEntity<String>> clz);

    /**
     * 按业务主键合并节点
     * <p>
     * 业务主键已存在则增量更新属性，不存在则创建。相比 {@link #insert} 的纯新增，
     * 合并可重复执行而不产生重复节点，适合数据同步场景。
     * </p>
     * <p>
     * 实体需用 {@code @Column(isPrimaryKey = true)} 声明业务主键，否则抛出业务异常。
     * 写入成功后会把图库生成的标识回写到实体的 {@code @GraphId} 字段。
     * </p>
     *
     * @param entities 节点实体集合
     * @return 全部实体都已写入返回 true；有实体未写入返回 false（执行出错会抛异常而非返回 false）
     */
    boolean merge(Collection<? extends IEntity<String>> entities);

    /**
     * 查询原生结果并交由处理器消费
     * <p>
     * 处理器入参为具体图数据库驱动的原生结果对象（Neo4j 为 {@code org.neo4j.driver.Result}），
     * 因此调用方会绑定到具体图数据库实现。仅在框架能力不足时使用，
     * 优先考虑 {@link #queryRecordList}。
     * </p>
     *
     * @param cypher        Cypher 查询语句
     * @param params        绑定参数，可为 null
     * @param resultHandler 原生结果处理器
     * @param <R>           处理结果类型
     * @return 处理器的返回值
     */
    <R> R queryNative(String cypher, Map<String, Object> params, Function<Object, R> resultHandler);

    /**
     * 查询多条记录（无参数）
     *
     * @param cypher Cypher 查询语句
     * @param clz    返回值类型
     * @param <T>    返回值类型泛型
     * @return 查询结果列表
     */
    default <T> List<T> queryList(final String cypher, final Class<T> clz) {
        return queryList(cypher, Collections.emptyMap(), clz);
    }

    /**
     * 查询单条记录（无参数）
     *
     * @param cypher Cypher 查询语句
     * @param clz    返回值类型
     * @param <T>    返回值类型泛型
     * @return 查询结果，无结果返回 null
     */
    default <T> T queryOne(final String cypher, final Class<T> clz) {
        return queryOne(cypher, Collections.emptyMap(), clz);
    }

    /**
     * 查询单行单列值（无参数）
     *
     * @param cypher Cypher 查询语句
     * @param clz    返回值类型
     * @param <T>    返回值类型泛型
     * @return 查询结果，无结果返回 null
     */
    default <T> T queryObject(final String cypher, final Class<T> clz) {
        return queryObject(cypher, Collections.emptyMap(), clz);
    }

    /**
     * 执行写语句（无参数）
     *
     * @param cypher Cypher 写语句
     * @return 受影响的记录数
     */
    default long execute(final String cypher) {
        return execute(cypher, Collections.emptyMap());
    }
}
