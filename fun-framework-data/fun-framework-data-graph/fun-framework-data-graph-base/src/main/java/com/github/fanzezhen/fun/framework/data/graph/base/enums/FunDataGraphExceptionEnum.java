package com.github.fanzezhen.fun.framework.data.graph.base.enums;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.IExceptionCode;
import lombok.Getter;

/**
 * 图数据库模块异常枚举
 * <p>
 * 只定义图数据库特有的异常，错误码格式 123**。通用数据异常
 * （标识符非法、主键缺失、结果解析失败、多数据源模板相关）统一用
 * {@link com.github.fanzezhen.fun.framework.core.model.enums.FunCoreDataExceptionEnum}。
 * </p>
 *
 * @since 4.1.1
 */
@Getter
public enum FunDataGraphExceptionEnum implements IExceptionCode<FunDataGraphExceptionEnum> {

    /**
     * 图查询执行失败
     * <p>错误信息参数：</p>
     * <ol>
     *   <li>图数据库标识</li>
     *   <li>详细错误信息</li>
     * </ol>
     */
    QUERY_EXECUTE_FAILED(12300, "%s图数据库执行失败：%s"),

    /**
     * 图数据库功能不支持
     * <p>错误信息参数：</p>
     * <ol>
     *   <li>图数据库标识</li>
     *   <li>不支持的功能名</li>
     * </ol>
     */
    FEATURE_NOT_SUPPORTED(12301, "%s图数据库功能不支持：%s"),

    /**
     * 实体未标注图注解
     * <p>错误信息参数：</p>
     * <ol>
     *   <li>实体类名</li>
     * </ol>
     */
    GRAPH_ENTITY_ANNOTATION_MISSING(12302, "实体 %s 未标注 @GraphNode 或 @GraphRelationship 注解"),
    ;

    /**
     * 构造方法
     *
     * @param code 错误码
     * @param text 错误信息模板
     */
    FunDataGraphExceptionEnum(final int code, final String text) {
        this.code = code;
        this.text = text;
    }

    /**
     * 错误码
     */
    @JsonValue
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteEnumUsingToString)
    private final Integer code;

    /**
     * 错误信息模板
     */
    private final String text;
}
