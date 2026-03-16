package com.github.fanzezhen.fun.framework.core.model.bucket;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.swing.*;

/**
 *
 */
@Data
@Accessors(chain = true)
public class AggregationCondition {

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 序
     */
    private SortOrder sortOrder;

    /**
     * 序
     */
    private Integer limit;
}
