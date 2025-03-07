package com.github.fanzezhen.fun.framework.trace.model.bo;

import com.github.fanzezhen.fun.framework.core.model.bo.BaseBO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 痕迹规则
 *
 * @author fanzezhen
 * @since 3.4.3.1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class TraceRuleBO extends BaseBO<String> {

    /**
     * 名称
     */
    private String name;
    /**
     * 名称字段
     */
    private String nameKey;
    /**
     * 父级
     */
    private String parent;
    /**
     * 父级字段
     */
    private String parentKey;
    /**
     * 项目字段
     */
    private String projectKey;
    /**
     * 明细字段
     */
    private Map<String, String> detail;

}
