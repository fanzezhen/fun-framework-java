package com.github.fanzezhen.fun.framework.core.model.constant;

import cn.hutool.core.text.CharSequenceUtil;

/**
 * Fun Framework 核心数据常量.
 * <p>
 * 定义框架核心模块的数据相关常量，如默认数据源名称等。
 */
public final class FunFrameworkCoreDataConstant {
    /**
     * 默认数据源名称.
     * <p>
     * 空字符串表示使用框架的默认数据源配置。
     */
    public static final String DEFAULT_DATASOURCE_NAME = CharSequenceUtil.EMPTY;

    /**
     * 工具类不允许实例化.
     */
    private FunFrameworkCoreDataConstant() {
        throw new UnsupportedOperationException("Utility class");
    }
}
