package com.github.fanzezhen.fun.framework.data.graph.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 图关系类型注解
 * <p>
 * 标记字段用于承载关系的实际类型，字段类型应为 {@link java.lang.String}。
 * 与 {@link GraphLabels} 对称：前者承载节点标签，后者承载关系类型，
 * 二者都由图库返回值填充，写入语句不会把它们当作普通属性。
 * </p>
 *
 * @since 4.1.1
 */
@Documented
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphType {
}
