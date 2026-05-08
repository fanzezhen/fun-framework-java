package com.github.fanzezhen.fun.framework.mp.base;

import com.baomidou.mybatisplus.extension.parser.JsqlParserGlobal;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.Statement;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * MyBatis工具类
 * <p>
 * 提供SQL解析功能，将MyBatis的BoundSql转换为JSQLParser的Statement对象，
 * 用于SQL改写、分析等高级场景（如分表路由、权限过滤）。
 *
 */
@Slf4j
public class MybatisUtil {

    public static Statement getStatement(MappedStatement mappedStatement, Object arg1) {
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
