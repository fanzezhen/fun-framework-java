/**
 * 图数据库模型包
 * <p>
 * 本包定义两类模型：
 * </p>
 * <ul>
 *   <li>中间表示：{@link com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRecordData}、
 *       {@link com.github.fanzezhen.fun.framework.data.graph.base.model.GraphNodeData}、
 *       {@link com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRelationshipData}，
 *       是抽象层与图数据库驱动之间的唯一数据边界，各 starter 负责把驱动返回值归一化为这些类型</li>
 *   <li>实体基类：{@link com.github.fanzezhen.fun.framework.data.graph.base.model.BaseNode}、
 *       {@link com.github.fanzezhen.fun.framework.data.graph.base.model.BaseRelationship}，
 *       供业务实体按需继承</li>
 * </ul>
 */
package com.github.fanzezhen.fun.framework.data.graph.base.model;
