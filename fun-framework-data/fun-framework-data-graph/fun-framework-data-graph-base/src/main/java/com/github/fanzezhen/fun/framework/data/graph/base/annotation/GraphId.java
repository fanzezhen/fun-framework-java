package com.github.fanzezhen.fun.framework.data.graph.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 图库内部标识注解
 * <p>
 * 标记字段承载图库生成的内部标识（Neo4j 的 elementId）。该标识由图库分配，
 * 写入语句不带该字段，写入完成后回填。
 * </p>
 * <p>
 * 业务主键不用本注解，用框架统一的
 * {@link com.github.fanzezhen.fun.framework.core.model.annotation.Column}
 * 标注 {@code @Column(name = "xxx", isPrimaryKey = true)}，
 * 与其他数据模块保持一致。
 * </p>
 *
 * @since 4.1.1
 */
@Documented
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphId {
}
