package com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 高亮字段注解
 * <p>
 * 用于标记接收 Elasticsearch 高亮查询结果的字段。
 * 被标注的字段必须是 {@code Map<String, List<String>>} 类型，
 * 其中 key 为 ES 字段名，value 为加了高亮标签的值列表。
 * </p>
 * <p>使用示例：</p>
 * <pre>
 * public class SearchResult {
 *     private String title;
 *     private String content;
 *
 *     {@literal @}HighlightField
 *     private Map&lt;String, List&lt;String&gt;&gt; highlightFields;
 * }
 * </pre>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface HighlightField {
}
