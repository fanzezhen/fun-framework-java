package com.github.fanzezhen.fun.framework.core.data.enums;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.IExceptionCode;
import lombok.Getter;

/**
 * 异常枚举
 *
 * @author fanzezhen
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
