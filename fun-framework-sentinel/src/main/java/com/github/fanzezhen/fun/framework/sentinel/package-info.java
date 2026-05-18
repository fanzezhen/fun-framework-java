/**
 * Sentinel 限流降级集成模块。
 * <p>
 * 提供 Alibaba Sentinel 的数据源初始化功能，支持从文件或 Nacos 加载规则配置。
 * <p>
 * 通过实现 Sentinel 的 SPI 机制 {@link com.alibaba.csp.sentinel.init.InitFunc}，
 * 在应用启动时自动初始化流控规则、降级规则、系统规则和授权规则。
 */
package com.github.fanzezhen.fun.framework.sentinel;
