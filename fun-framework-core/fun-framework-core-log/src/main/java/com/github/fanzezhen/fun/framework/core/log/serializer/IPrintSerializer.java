package com.github.fanzezhen.fun.framework.core.log.serializer;

/**
 * 日志对象序列化器（策略模式）
 * <p>
 * 用于将特殊类型对象（文件流、字节数组等）转换为可打印的日志字符串。
 * 实现类通过isSupport方法声明支持的类型，运行时动态选择合适的序列化器。
 * <p>
 * <b>使用场景：</b>HTTP请求日志、文件上传日志、二进制数据日志
 *
 */
public interface IPrintSerializer {

    /**
     * 是否可以解析
     *
     * @param o 对象
     */
    boolean isSupport(Object o);

    /**
     * 序列化
     *
     * @param o 对象
     */
    String serialize(Object o);

}
