package com.github.fanzezhen.fun.framework.core.model.enums;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.IExceptionCode;
import lombok.Getter;

/**
 * 异常枚举
 *
 * @since 3
 */
@Getter
public enum FunCoreDataExceptionEnum implements IExceptionCode<FunCoreDataExceptionEnum> {

    TABLE_IS_NULL(100601, "：%s"),
    TEMPLATE_IMPL_NAME_DUPLICATED(100601, "操作模板实例名称重复：%s"),
    TEMPLATE_IMPL_CREATE_NULL(100602, "操作模板实例创建为null：%s"),
    TEMPLATE_IMPL_NOT_EXISTS(100603, "操作模板实例 %s 不存在"),
    TEMPLATE_IMPL_CONFIG_NOT_EXISTS(100604, "操作模板实例 %s配置 不存在"),
    DATA_RESULT_DESERIALIZE_FAILED(100605, "%s数据结果解析失败：%s"),

    /**
     * 数据标识符非法
     * <p>
     * 表名、列名、图标签、关系类型这类标识符无法通过参数绑定，只能进入语句文本，
     * 因此拼接前强制校验。参数：1 标识符类别，2 非法标识符。
     * </p>
     *
     * @since 4.1.1
     */
    ILLEGAL_IDENTIFIER(100606, "数据标识符%s非法：%s，只允许字母、数字、下划线且不以数字开头"),

    /**
     * 实体缺少主键声明
     * <p>
     * 参数：1 实体类名。
     * </p>
     *
     * @since 4.1.1
     */
    PRIMARY_KEY_MISSING(100607, "实体 %s 未声明主键，需用 @Column(isPrimaryKey = true) 标注字段"),
    ;

    FunCoreDataExceptionEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    @JsonValue
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteEnumUsingToString)
    private final Integer code;
    private final String text;
}
