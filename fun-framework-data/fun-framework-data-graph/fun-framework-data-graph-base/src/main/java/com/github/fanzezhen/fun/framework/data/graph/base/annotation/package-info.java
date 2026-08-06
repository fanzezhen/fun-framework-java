/**
 * 图数据库注解包
 * <p>
 * 本包只定义图特有的映射注解，不依赖任何图数据库驱动，
 * 因此 Neo4j 与其他图数据库（含私有驱动图库）可复用同一套注解。
 * </p>
 * <p>
 * 属性名映射与业务主键复用框架统一的
 * {@link com.github.fanzezhen.fun.framework.core.model.annotation.Column}
 * （{@code name} / {@code isPrimaryKey} / {@code writable}），不另立图专属注解。
 * </p>
 * <p>主要注解：</p>
 * <ul>
 *   <li>{@link com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphNode} 节点实体</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphRelationship} 关系实体</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphId} 图库内部标识</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphLabels} 标签集合</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.data.graph.base.annotation.StartNode} 关系起始节点</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.data.graph.base.annotation.EndNode} 关系结束节点</li>
 * </ul>
 */
package com.github.fanzezhen.fun.framework.data.graph.base.annotation;
