package com.github.fanzezhen.fun.framework.proxy.mybatis;

import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;
import org.apache.ibatis.type.StringTypeHandler;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 代理字段类型处理器
 * <p>
 * 继承自 MyBatis 的 StringTypeHandler，在字符串字段从数据库读取时自动进行代理处理
 *
 * @since 3.4.3.5
 */
public class ProxyFieldStringTypeHandler extends StringTypeHandler {
    private static boolean enabled;
    private static ProxyHelper proxyHelper;

    /**
     * 从 ResultSet 中根据列名获取字符串值
     *
     * @param rs         结果集
     * @param columnName 列名
     * @return 处理后的字符串值
     * @throws SQLException SQL异常
     */
    @Override
    public String getResult(final ResultSet rs, final String columnName) throws SQLException {
        String result = super.getResult(rs, columnName);
        return enabled ? proxyHelper.decorateStr(result) : result;
    }

    /**
     * 从 ResultSet 中根据列索引获取字符串值
     *
     * @param rs          结果集
     * @param columnIndex 列索引
     * @return 处理后的字符串值
     * @throws SQLException SQL异常
     */
    @Override
    public String getResult(final ResultSet rs, final int columnIndex) throws SQLException {
        String result = super.getResult(rs, columnIndex);
        return enabled ? proxyHelper.decorateStr(result) : result;
    }

    /**
     * 从 CallableStatement 中根据列索引获取字符串值
     *
     * @param cs          调用语句
     * @param columnIndex 列索引
     * @return 处理后的字符串值
     * @throws SQLException SQL异常
     */
    @Override
    public String getResult(final CallableStatement cs, final int columnIndex) throws SQLException {
        String result = super.getResult(cs, columnIndex);
        return enabled ? proxyHelper.decorateStr(result) : result;
    }

    /**
     * 静态初始化方法，由配置类调用
     *
     * @param enabled     是否启用代理
     * @param proxyHelper 代理助手实例
     */
    public static void initStatic(final boolean enabled, final ProxyHelper proxyHelper) {
        ProxyFieldStringTypeHandler.enabled = enabled;
        ProxyFieldStringTypeHandler.proxyHelper = proxyHelper;
    }
}
