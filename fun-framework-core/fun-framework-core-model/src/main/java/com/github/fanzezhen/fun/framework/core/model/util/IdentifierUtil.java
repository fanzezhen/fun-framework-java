package com.github.fanzezhen.fun.framework.core.model.util;

import com.github.fanzezhen.fun.framework.core.model.enums.FunCoreDataExceptionEnum;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;

import java.util.regex.Pattern;

/**
 * 数据标识符工具
 * <p>
 * 表名、列名、图标签、关系类型这类标识符无法通过参数绑定，只能拼接进语句文本，
 * 是注入的主要入口。本工具提供统一的白名单校验：不合法直接拒绝而非转义——
 * 标识符本就不该出现需要转义的字符，拒绝比转义更能暴露上游的错误。
 * </p>
 *
 * @since 4.1.1
 */
public final class IdentifierUtil {

    /**
     * 标识符类别：表
     */
    public static final String CATEGORY_TABLE = "表名";

    /**
     * 标识符类别：列
     */
    public static final String CATEGORY_COLUMN = "列名";

    /**
     * 标识符类别：图节点标签
     */
    public static final String CATEGORY_LABEL = "标签";

    /**
     * 标识符类别：图关系类型
     */
    public static final String CATEGORY_RELATIONSHIP_TYPE = "关系类型";

    /**
     * 标识符类别：属性
     */
    public static final String CATEGORY_PROPERTY = "属性名";

    /**
     * 合法标识符：字母或下划线开头，其后为字母、数字、下划线
     */
    private static final Pattern LEGAL_PATTERN = Pattern.compile("^[A-Za-z_]\\w*$");

    /**
     * 反引号
     */
    private static final char BACKTICK = '`';

    private IdentifierUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 判断标识符是否合法
     *
     * @param name 标识符
     * @return true 表示合法
     */
    public static boolean isLegal(final String name) {
        return name != null && LEGAL_PATTERN.matcher(name).matches();
    }

    /**
     * 校验标识符合法性
     *
     * @param name     标识符
     * @param category 标识符类别，用于异常信息
     * @return 原标识符
     * @throws ServiceException 标识符不合法时抛出
     */
    public static String requireLegal(final String name, final String category) {
        if (!isLegal(name)) {
            throw new ServiceException(FunCoreDataExceptionEnum.ILLEGAL_IDENTIFIER, category, String.valueOf(name));
        }
        return name;
    }

    /**
     * 校验标识符并用反引号包裹
     * <p>
     * 校验通过的标识符本身无需转义，反引号仅用于规避与保留字冲突。
     * MySQL 与 Cypher 均以反引号作为标识符引用符。
     * </p>
     *
     * @param name     标识符
     * @param category 标识符类别，用于异常信息
     * @return 反引号包裹后的标识符
     * @throws ServiceException 标识符不合法时抛出
     */
    public static String quote(final String name, final String category) {
        return BACKTICK + requireLegal(name, category) + BACKTICK;
    }
}
