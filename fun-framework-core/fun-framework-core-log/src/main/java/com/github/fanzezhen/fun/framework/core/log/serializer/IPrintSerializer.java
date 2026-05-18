package com.github.fanzezhen.fun.framework.core.log.serializer;

/**
 * 日志对象序列化器接口（策略模式）.
 * <p>
 * 用于将特殊类型（文件流、字节数组等）转换为可打印的日志字符串。
 * 实现通过 isSupport 方法声明支持的类型；在运行时动态选择适当的序列化器。
 * <p>
 * <b>使用场景：</b>HTTP 请求日志记录、文件上传日志记录、二进制数据日志记录。
 */
public interface IPrintSerializer {

    /**
     * 检查此序列化器是否支持给定对象.
     *
     * @param o 要检查的对象
     * @return 如果此序列化器可以处理该对象则返回 true，否则返回 false
     */
    boolean isSupport(Object o);

    /**
     * 将对象序列化为可打印的字符串.
     *
     * @param o 要序列化的对象
     * @return 序列化后的字符串表示
     */
    String serialize(Object o);

}
