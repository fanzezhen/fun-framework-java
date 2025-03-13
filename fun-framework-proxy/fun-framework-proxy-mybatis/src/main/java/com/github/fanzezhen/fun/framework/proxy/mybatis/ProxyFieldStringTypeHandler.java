package com.github.fanzezhen.fun.framework.proxy.mybatis;

import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;
import org.apache.ibatis.type.StringTypeHandler;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 代理字段类型解析器
 *
 * @author fanzezhen
 * @since 3.4.3.5
 */
public class ProxyFieldStringTypeHandler extends StringTypeHandler {
    private static boolean enabled;
    private static ProxyHelper proxyHelper;

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        String result = super.getResult(rs, columnName);
        return enabled ? proxyHelper.decorateStr(result) : result;
    }

    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        String result = super.getResult(rs, columnIndex);
        return enabled ? proxyHelper.decorateStr(result) : result;
    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String result = super.getResult(cs, columnIndex);
        return enabled ? proxyHelper.decorateStr(result) : result;
    }

    public static void initStatic(boolean enabled, ProxyHelper proxyHelper) {
        ProxyFieldStringTypeHandler.enabled = enabled;
        ProxyFieldStringTypeHandler.proxyHelper = proxyHelper;
    }
}
