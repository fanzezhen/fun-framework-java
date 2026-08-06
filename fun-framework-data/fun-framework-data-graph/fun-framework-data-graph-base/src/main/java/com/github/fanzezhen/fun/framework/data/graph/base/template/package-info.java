/**
 * 图数据库模板包
 * <p>
 * 本包提供图操作的核心模板接口与抽象实现。
 * </p>
 * <p>主要组件：</p>
 * <ul>
 *   <li>{@link com.github.fanzezhen.fun.framework.data.graph.base.template.IGraphTemplate}
 *       统一操作接口，继承框架 {@code ITemplate} 的 CRUD 语义并扩展 Cypher 查询</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.data.graph.base.template.BaseGraphTemplate}
 *       抽象基类，把全部能力收敛到三个驱动原语上</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.data.graph.base.template.BaseMultiDatasourceGraphTemplate}
 *       多数据源模板，按 {@code @Entity(datasource)} 路由</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.data.graph.base.template.CypherBuilder}
 *       Cypher 语句构建器，属性值参数绑定、图元素名白名单校验</li>
 * </ul>
 */
package com.github.fanzezhen.fun.framework.data.graph.base.template;
