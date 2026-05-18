package com.github.fanzezhen.fun.framework.mp.base;

import com.baomidou.mybatisplus.extension.parser.JsqlParserGlobal;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.Statement;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * MyBatis 工具类
 * <p>
 * 提供 SQL 解析功能，将 MyBatis 的 BoundSql 转换为 JSQLParser 的 Statement 对象，
 * 用于 SQL 改写、分析等高级场景（如分表路由、权限过滤）。
 */
@Slf4j
public final class MybatisUtil {

    /**
     * 私有构造函数，防止实例化
     */
    private MybatisUtil() {}

    /**
     * 从 MappedStatement 中获取解析后的 SQL Statement 对象
     * <p>
     * 将 MyBatis 的 BoundSql 转换为 JSQLParser 的 Statement 对象，
     * 便于进行 SQL 改写、权限过滤等操作。
     *
     * @param mappedStatement MyBatis 映射语句
     * @param arg1 方法参数
     * @return 解析后的 SQL Statement 对象，解析失败时返回 null
     */
    public static Statement getStatement(final MappedStatement mappedStatement, final Object arg1) {
        BoundSql boundSql = mappedStatement.getBoundSql(arg1);
        String sql = boundSql.getSql();
        try {
            return JsqlParserGlobal.parse(sql);
        } catch (JSQLParserException e) {
            log.warn("sql parse error {}", sql, e);
        }
        return null;
    }
}
