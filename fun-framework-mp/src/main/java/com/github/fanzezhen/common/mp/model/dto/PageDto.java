package com.github.fanzezhen.fun.framework.mp.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zezhen.fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PageDto<T extends G, G> extends Page<G> {
    private T param;
}
