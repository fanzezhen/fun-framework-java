package com.github.fanzezhen.fun.framework.mp.trace.model.bo;

import com.github.fanzezhen.fun.framework.core.model.bo.BaseBO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 痕迹表
 *
 * @author fanzezhen
 * @createTime 2025-01-13 17:12:18
 * @since 3.4.3.1
 */
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
