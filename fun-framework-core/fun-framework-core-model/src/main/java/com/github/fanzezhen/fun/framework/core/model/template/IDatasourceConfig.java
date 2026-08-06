package com.github.fanzezhen.fun.framework.core.model.template;

/**
 * 数据源配置契约
 * <p>
 * 多数据源模板按名称索引各数据源配置，因此只要求配置对象能给出名称。
 * 定义为接口而非基类：各存储的配置类由 Spring Boot 绑定，
 * 继承体系会牵扯 Lombok 生成的 setter 与 JavaBeanBinder 的属性判定，
 * 实现接口则完全无绑定风险。
 * </p>
 *
 * @since 4.1.1
 */
public interface IDatasourceConfig {

    /**
     * 获取数据源名称
     *
     * @return 数据源名称
     */
    String getName();
}
