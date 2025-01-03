package com.github.fanzezhen.fun.framework.core.model.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author fanzezhen
 * @date 2017/12/19
 * Desc:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeBean {
    private String type;
    private String name;
    private Object value;
}
