package com.github.fanzezhen.fun.framework.core.model.condition;

import com.github.fanzezhen.fun.framework.core.model.common.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PageCondition implements IPage {

    /**
     * 页码
     */
    protected int current;

    /**
     * 每页显示条数
     */
    protected int size;
}
