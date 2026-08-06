package com.github.fanzezhen.fun.framework.data.graph.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 图节点注解
 * <p>
 * 标记类为图数据库的节点实体。映射时用于确定节点标签（label），
 * 未指定 {@link #label()} 时取类简名。
 * </p>
 *
 * @since 4.1.1
 */
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphNode {

    /**
     * 节点标签
     * <p>
     * 为空时取类简名作为标签。标签无法通过 Cypher 参数绑定，
     * 拼接前会做合法性校验，只允许字母、数字、下划线，且不以数字开头。
     * </p>
     *
     * @return 节点标签
     */
    String label() default "";
}
