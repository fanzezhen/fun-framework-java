package com.github.fanzezhen.fun.framework.data.graph.base.mapping;

import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRecordData;

import java.util.List;

/**
 * 图结果映射引擎接口
 * <p>
 * 负责把驱动无关的 {@link GraphRecordData} 映射为业务对象。映射引擎不接触任何
 * 图数据库驱动类型，因此同一实现可服务于 Neo4j 及其他图数据库。
 * </p>
 * <p>
 * 子项目注册自定义 {@code IGraphMapper} bean 即可覆盖框架默认实现。
 * </p>
 *
 * @since 4.1.1
 */
public interface IGraphMapper {

    /**
     * 将结果行映射为对象
     * <p>
     * 目标类型为图实体（标注 {@code @GraphNode} 或 {@code @GraphRelationship}）时，
     * 优先取结果行中的节点/关系列直接映射，对应 {@code RETURN n} 这类查询；
     * 否则按列名映射到同名（或 {@code @Column(name)} 指定名）的字段，
     * 对应 {@code RETURN n.name AS name, count(*) AS total} 这类投影查询。
     * </p>
     *
     * @param recordData 结果行
     * @param clz    目标类型
     * @param <T>    目标类型泛型
     * @return 映射结果，入参为 null 时返回 null
     */
    <T> T mapOne(GraphRecordData recordData, Class<T> clz);

    /**
     * 将多行结果映射为对象列表
     *
     * @param recordList 结果行列表
     * @param clz        目标类型
     * @param <T>        目标类型泛型
     * @return 映射结果列表，入参为空时返回空列表
     */
    <T> List<T> mapList(List<GraphRecordData> recordList, Class<T> clz);

    /**
     * 将结果行的首列映射为单个对象
     * <p>
     * 用于 {@code RETURN count(n)}、{@code RETURN n.name} 这类单列查询，
     * 首列为节点或关系时映射为对应实体，否则按目标类型做类型转换。
     * </p>
     *
     * @param recordData 结果行
     * @param clz    目标类型
     * @param <T>    目标类型泛型
     * @return 映射结果，无值时返回 null
     */
    <T> T mapObject(GraphRecordData recordData, Class<T> clz);
}
