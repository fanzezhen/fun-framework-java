/**
 * Elasticsearch 配置包
 * <p>
 * 本包提供 Elasticsearch 的自动配置和属性管理功能，支持多数据源配置。
 * </p>
 * <p>主要功能：</p>
 * <ul>
 *   <li>自动配置 Elasticsearch 客户端和相关组件</li>
 *   <li>支持多数据源配置，每个数据源可以独立配置连接参数</li>
 *   <li>提供连接超时、请求超时、心跳等参数的细粒度控制</li>
 *   <li>支持 QPS 限流和窗口大小限制</li>
 * </ul>
 */
package com.github.fanzezhen.fun.framework.data.elasticsearch.base.config;
