package com.github.fanzezhen.fun.framework.api.count;

/**
 * API统计结果解析器
 * <p>
 * 用于从Controller返回值中提取实际数据对象，支持自定义统一返回体的拆箱逻辑。
 * 默认实现会自动识别 {@link com.github.fanzezhen.fun.framework.core.model.response.ActionResult} 并提取data字段。
 *
 * @since 3.4.3.3
 */
public interface FunApiCountResultResolve {
    /**
     * 从Controller返回值中提取实际数据对象
     * <p>
     * 对于统一返回体（如ActionResult），应提取其中的data字段；
     * 对于直接返回的实体，则原样返回。
     *
     * @param result Controller方法的返回值
     * @return 提取后的实际数据对象，用于后续字段统计分析
     */
    Object unseal(Object result);
}
