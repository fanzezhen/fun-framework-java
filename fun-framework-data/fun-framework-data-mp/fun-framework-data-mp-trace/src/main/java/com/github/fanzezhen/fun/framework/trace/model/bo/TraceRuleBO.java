package com.github.fanzezhen.fun.framework.trace.model.bo;

import com.github.fanzezhen.fun.framework.core.model.bo.BaseBO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 数据追踪规则业务对象
 * <p>
 * 定义单个表的数据追踪规则，包括追踪的业务名称、关键字段和明细字段映射。
 * 用于配置哪些字段需要记录变更历史，以及如何展示这些字段。
 * <p>
 * 使用场景：
 * <ul>
 *   <li>配置表级追踪规则（如用户表、订单表的变更追踪）</li>
 *   <li>定义字段级追踪粒度（哪些字段需要记录）</li>
 *   <li>设置字段显示名称（用于审计日志展示）</li>
 * </ul>
 *
 * @since 3.4.3.1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class TraceRuleBO extends BaseBO<String> {

    /**
     * 业务名称
     * <p>
     * 用于标识追踪的业务对象类型，如"用户信息"、"订单数据"等。
     * 在审计日志中作为追踪记录的主题显示。
     */
    private String name;

    /**
     * 名称字段
     * <p>
     * 指定作为业务对象标识的字段名（数据库列名），如用户表的 username 字段。
     * 该字段的值会被记录到追踪主表中，用于快速识别被追踪的数据。
     */
    private String nameKey;

    /**
     * 明细字段映射
     * <p>
     * key: 数据库列名（如 email、phone）<br>
     * value: 字段显示名称（如"邮箱"、"手机号"）
     * <p>
     * 用于配置需要追踪变更的字段及其在审计日志中的显示名称。
     */
    private Map<String, String> detail;

}
