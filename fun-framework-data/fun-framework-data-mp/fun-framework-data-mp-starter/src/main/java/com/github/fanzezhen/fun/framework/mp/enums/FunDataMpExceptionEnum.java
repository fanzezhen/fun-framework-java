package com.github.fanzezhen.fun.framework.mp.enums;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.IExceptionCode;
import lombok.Getter;

/**
 * MyBatis-Plus 模块异常枚举
 * <p>
 * 只定义本模块特有的异常，错误码格式 121**。通用数据异常（标识符非法、主键缺失、
 * 结果解析失败）统一用
 * {@link com.github.fanzezhen.fun.framework.core.model.enums.FunCoreDataExceptionEnum}。
 * </p>
 *
 * @since 4.1.1
 */
@Getter
public enum FunDataMpExceptionEnum implements IExceptionCode<FunDataMpExceptionEnum> {

    /**
     * 租户上下文缺失且策略为拒绝
     * <p>
     * 仅 {@code fun.mp.tenant.missing-strategy=reject} 时抛出。
     * </p>
     */
    TENANT_CONTEXT_MISSING(12100, "租户上下文缺失，无法确定数据归属"),

    /**
     * 租户 ID 无法解析为数值
     * <p>错误信息参数：</p>
     * <ol>
     *   <li>租户 ID 原值</li>
     * </ol>
     */
    TENANT_ID_NOT_NUMERIC(12101, "租户ID %s 无法解析为数值，请核对 fun.mp.tenant.value-type 与租户列类型是否匹配"),
    ;

    /**
     * 构造方法
     *
     * @param code 错误码
     * @param text 错误信息模板
     */
    FunDataMpExceptionEnum(final int code, final String text) {
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
