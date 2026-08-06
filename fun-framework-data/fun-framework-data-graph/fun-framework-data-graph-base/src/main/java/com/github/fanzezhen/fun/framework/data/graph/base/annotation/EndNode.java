package com.github.fanzezhen.fun.framework.data.graph.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 关系结束节点注解
 * <p>
 * 标记关系实体中承载结束节点的字段。字段类型应为节点实体类型。
 * </p>
 *
 * @since 4.1.1
 */
@Documented
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EndNode {
}
