package com.github.fanzezhen.fun.framework.core.web.mvc.response;

/**
 * 统一响应体包装器接口，用于将Controller返回值包装为统一格式（如 ActionResult）
 * <p>
 * 设计意图：避免重复包装导致的嵌套结构（如 ActionResult 套 ActionResult）
 * <pre>{@code
 * // 错误示例（未使用isWrapped检查）
 * {"code":200, "data":{"code":200, "data":"实际数据"}}  // 重复包装
 *
 * // 正确示例（使用isWrapped检查）
 * {"code":200, "data":"实际数据"}  // 单层包装
 * }</pre>
 * <p>
 * 典型实现：
 * <pre>{@code
 * public class ActionResultWrapper implements ResponseBodyWrapper {
 *     public boolean isWrapped(Object data) {
 *         return data instanceof ActionResult;  // 已是统一格式则跳过
 *     }
 *     public Object wrap(Object data) {
 *         return ActionResult.ok(data);
 *     }
 * }
 * }</pre>
 *
 * @since 3.4.3.5
 */
public interface ResponseBodyWrapper {
    /**
     * 判断返回值是否已包装为统一格式（如已是ActionResult则无需再次包装）
     */
    boolean isWrapped(Object data);

    /**
     * 将原始返回值包装为统一响应格式
     */
    Object wrap(Object data);
}
