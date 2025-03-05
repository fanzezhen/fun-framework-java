package com.github.fanzezhen.fun.framework.mp.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author zezhen.fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PageDto<T extends G, G extends Serializable> extends Page<G> {
    private T param;
}
