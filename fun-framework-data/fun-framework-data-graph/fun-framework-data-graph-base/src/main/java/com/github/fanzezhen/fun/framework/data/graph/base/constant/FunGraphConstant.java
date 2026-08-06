package com.github.fanzezhen.fun.framework.data.graph.base.constant;

/**
 * 图数据库常量
 * <p>
 * 集中定义中间表示中约定的键名，供各图数据库 starter 在归一化时复用，
 * 避免键名散落在各实现中导致不一致。
 * </p>
 *
 * @since 4.1.1
 */
public final class FunGraphConstant {

    /**
     * 路径归一化后的节点列表键
     */
    public static final String PATH_NODES = "nodes";

    /**
     * 路径归一化后的关系列表键
     */
    public static final String PATH_RELATIONSHIPS = "relationships";

    /**
     * 空间点归一化后的坐标系标识键
     */
    public static final String POINT_SRID = "srid";

    /**
     * 空间点归一化后的 X 坐标键
     */
    public static final String POINT_X = "x";

    /**
     * 空间点归一化后的 Y 坐标键
     */
    public static final String POINT_Y = "y";

    /**
     * 空间点归一化后的 Z 坐标键
     */
    public static final String POINT_Z = "z";

    /**
     * 时长归一化后的月数键
     */
    public static final String DURATION_MONTHS = "months";

    /**
     * 时长归一化后的天数键
     */
    public static final String DURATION_DAYS = "days";

    /**
     * 时长归一化后的秒数键
     */
    public static final String DURATION_SECONDS = "seconds";

    /**
     * 时长归一化后的纳秒数键
     */
    public static final String DURATION_NANOSECONDS = "nanoseconds";

    private FunGraphConstant() {
    }
}
