package com.github.fanzezhen.fun.framework.data.graph.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 图关系注解
 * <p>
 * 标记类为图数据库的关系实体。映射时用于确定关系类型（type），
 * 未指定 {@link #type()} 时取类简名。
 * </p>
 *
 * @since 4.1.1
 */
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphRelationship {

    /**
     * 关系类型
     * <p>
     * 为空时取类简名作为类型。与节点标签同理，拼接前会做合法性校验。
     * </p>
     *
     * @return 关系类型
     */
    String type() default "";
}
