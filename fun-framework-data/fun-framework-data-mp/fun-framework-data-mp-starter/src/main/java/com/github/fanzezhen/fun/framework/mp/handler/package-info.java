/**
 * MyBatis 类型处理器包
 * <p>
 * 提供自定义的 MyBatis 类型处理器，用于数据库字段与 Java 对象之间的类型转换。
 * 主要类包括：
 * <ul>
 *   <li>{@link com.github.fanzezhen.fun.framework.mp.handler.AbstractJsonTypeHandler} - JSON 类型处理器抽象基类</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.mp.handler.AbstractFastjsonTypeHandler} - 基于 Fastjson 的类型处理器抽象基类</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.mp.handler.JsonObjTypeHandler} - JSON 对象类型处理器</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.mp.handler.JsonArrTypeHandler} - JSON 数组类型处理器</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.mp.handler.JsonNodeTypeHandler} - JSON 节点类型处理器</li>
 * </ul>
 * 适用场景：需要在数据库中存储 JSON 格式数据时使用。
 */
package com.github.fanzezhen.fun.framework.mp.handler;
