package com.github.fanzezhen.fun.framework.api.count;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.fanzezhen.fun.framework.core.model.response.ActionResult;

/**
 * @author fanzezhen
 * @createTime 2025/3/7 17:57
 * @since 1.0.0
 */
public class FunApiCountResultResolveImpl implements FunApiCountResultResolve{
    @Override
    public Object unseal(Object result) {
        Object data = result;
        if (data instanceof ActionResult<?> actionResult) {
            data = actionResult.getData();
        }
        if (data instanceof PageDTO<?> pageDTO) {
            data = pageDTO.getRecords();
        }
        return data;
    }
}
