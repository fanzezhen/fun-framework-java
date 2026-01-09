package com.github.fanzezhen.fun.framework.core.log.base.serializer;

/**
 * 日志序列化器
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
