package com.github.fanzezhen.fun.framework.mp.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * JSON 类型处理器抽象基类
 * <p>
 * 提供 MyBatis 类型处理器的抽象实现，用于将数据库中的 JSON 字符串字段与 Java 对象之间进行转换。
 * 子类需要实现 {@link #parse(String)} 和 {@link #toJson(Object)} 方法来指定具体的 JSON 序列化/反序列化逻辑。
 * <p>
 * 使用场景：
 * <ul>
 *   <li>数据库字段存储 JSON 格式数据</li>
 *   <li>需要在查询时自动将 JSON 转换为 Java 对象</li>
 *   <li>需要在插入/更新时自动将 Java 对象转换为 JSON</li>
 * </ul>
 *
 * @param <T> Java 对象类型
 */
public abstract class AbstractJsonTypeHandler<T> extends BaseTypeHandler<T> {

    /**
     * Java 对象的类型
     */
    protected Class<T> classType;

    /**
     * 构造函数
     *
     * @param classType Java 对象的类型
     */
    protected AbstractJsonTypeHandler(final Class<T> classType) {
        this.classType = classType;
    }

    /**
     * 设置非空参数
     * <p>
     * 将 Java 对象转换为 JSON 字符串并设置到 PreparedStatement 中。
     *
     * @param ps PreparedStatement 对象
     * @param i 参数索引
     * @param parameter 参数值
     * @param jdbcType JDBC 类型
     * @throws SQLException SQL 异常
     */
    @Override
    public void setNonNullParameter(final PreparedStatement ps, final int i, final T parameter, final JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    /**
     * 根据列名获取结果
     *
     * @param rs ResultSet 对象
     * @param columnName 列名
     * @return 解析后的 Java 对象，JSON 为空时返回 null
     * @throws SQLException SQL 异常
     */
    @Override
    public T getNullableResult(final ResultSet rs, final String columnName) throws SQLException {
        final String json = rs.getString(columnName);
        return StringUtils.isBlank(json) ? null : parse(json);
    }

    /**
     * 根据列索引获取结果
     *
     * @param rs ResultSet 对象
     * @param columnIndex 列索引
     * @return 解析后的 Java 对象，JSON 为空时返回 null
     * @throws SQLException SQL 异常
     */
    @Override
    public T getNullableResult(final ResultSet rs, final int columnIndex) throws SQLException {
        final String json = rs.getString(columnIndex);
        return StringUtils.isBlank(json) ? null : parse(json);
    }

    /**
     * 从 CallableStatement 获取结果
     *
     * @param cs CallableStatement 对象
     * @param columnIndex 列索引
     * @return 解析后的 Java 对象，JSON 为空时返回 null
     * @throws SQLException SQL 异常
     */
    @Override
    public T getNullableResult(final CallableStatement cs, final int columnIndex) throws SQLException {
        final String json = cs.getString(columnIndex);
        return StringUtils.isBlank(json) ? null : parse(json);
    }

    /**
     * 将 JSON 字符串解析为 Java 对象
     * <p>
     * 子类需要实现此方法，指定具体的 JSON 解析逻辑。
     *
     * @param json JSON 字符串
     * @return 解析后的 Java 对象
     */
    protected abstract T parse(String json);

    /**
     * 将 Java 对象转换为 JSON 字符串
     * <p>
     * 子类需要实现此方法，指定具体的 JSON 序列化逻辑。
     *
     * @param obj Java 对象
     * @return JSON 字符串
     */
    protected abstract String toJson(T obj);
}
