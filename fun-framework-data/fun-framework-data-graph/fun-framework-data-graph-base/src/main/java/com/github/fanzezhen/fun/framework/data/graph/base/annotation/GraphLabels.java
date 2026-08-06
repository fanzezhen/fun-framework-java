package com.github.fanzezhen.fun.framework.data.graph.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 图标签集合注解
 * <p>
 * 标记字段用于承载节点的全部标签。字段类型应为 {@code Collection<String>}，
 * 映射时写入节点的实际标签集合，写入语句不会把该字段当作普通属性。
 * </p>
 *
 * @since 4.1.1
 */
@Documented
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphLabels {
}
