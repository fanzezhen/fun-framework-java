/**
 * 自增主键策略多租户实体基类包
 * <p>
 * 提供基于自增主键策略的多租户实体基类，主键类型为 Integer，额外包含租户 ID 字段。
 * 主要类包括：
 * <ul>
 *   <li>{@link com.github.fanzezhen.fun.framework.mp.base.entity.increment.tenant.BaseTenantEntity} - 自增主键多租户实体基类</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.mp.base.entity.increment.tenant.BaseTenantGenericEntity} - 自增主键多租户泛型实体基类</li>
 * </ul>
 * 适用场景：需要多租户数据隔离的单体应用。
 */
package com.github.fanzezhen.fun.framework.mp.base.entity.increment.tenant;
