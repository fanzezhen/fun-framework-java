package com.github.fanzezhen.fun.framework.data.graph.base.template;

import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.model.util.IdentifierUtil;

/**
 * Cypher 语句构建器
 * <p>
 * 生成基础 CRUD 的 Cypher 语句。属性值一律走参数绑定，不做字符串拼接；
 * 标签、关系类型与属性名无法参数化，只能进入语句文本，因此拼接前经
 * {@link IdentifierUtil} 做白名单校验，不合法直接抛异常而非转义。
 * </p>
 *
 * @since 4.1.1
 */
public final class CypherBuilder {

    /**
     * 单值参数名
     */
    public static final String PARAM_VALUE = "value";

    /**
     * 多值参数名
     */
    public static final String PARAM_VALUES = "values";

    /**
     * 单标识参数名
     */
    public static final String PARAM_ID = "id";

    /**
     * 多标识参数名
     */
    public static final String PARAM_IDS = "ids";

    /**
     * 批量行参数名
     */
    public static final String PARAM_ROWS = "rows";

    /**
     * 关系写入行中的起始节点标识键
     */
    public static final String ROW_START_ID = "startId";

    /**
     * 关系写入行中的结束节点标识键
     */
    public static final String ROW_END_ID = "endId";

    /**
     * 关系写入行中的属性映射键
     */
    public static final String ROW_PROPERTIES = "props";

    /**
     * 节点匹配子句前缀
     */
    private static final String MATCH_NODE_PREFIX = "MATCH (n:";

    /**
     * 节点属性条件前缀
     */
    private static final String WHERE_NODE_PROPERTY = ") WHERE n.";

    /**
     * 返回节点子句
     */
    private static final String RETURN_NODE = " RETURN n";

    /**
     * 批量展开子句前缀
     */
    private static final String UNWIND_ROWS = "UNWIND $" + PARAM_ROWS + " AS row ";

    /**
     * 图库标识匹配条件前缀
     */
    private static final String WHERE_ELEMENT_ID = ") WHERE elementId(n) ";

    /**
     * 返回图库标识子句
     */
    private static final String RETURN_ELEMENT_ID = " RETURN elementId(n)";

    private CypherBuilder() {
    }

    /**
     * 按属性单值匹配节点
     *
     * @param label     节点标签
     * @param property  属性名
     * @param paramName 绑定参数名
     * @return Cypher 语句
     */
    public static String matchByProperty(final String label, final String property, final String paramName) {
        return MATCH_NODE_PREFIX + quoteLabel(label) + WHERE_NODE_PROPERTY + quoteProperty(property)
            + " = $" + paramName + RETURN_NODE;
    }

    /**
     * 按属性多值匹配节点
     *
     * @param label     节点标签
     * @param property  属性名
     * @param paramName 绑定参数名
     * @return Cypher 语句
     */
    public static String matchByPropertyIn(final String label, final String property, final String paramName) {
        return MATCH_NODE_PREFIX + quoteLabel(label) + WHERE_NODE_PROPERTY + quoteProperty(property)
            + " IN $" + paramName + RETURN_NODE;
    }

    /**
     * 按图库标识匹配节点
     *
     * @param label     节点标签
     * @param paramName 绑定参数名
     * @return Cypher 语句
     */
    public static String matchByElementId(final String label, final String paramName) {
        return MATCH_NODE_PREFIX + quoteLabel(label) + WHERE_ELEMENT_ID + "= $" + paramName + RETURN_NODE;
    }

    /**
     * 按图库标识集合匹配节点
     *
     * @param label     节点标签
     * @param paramName 绑定参数名
     * @return Cypher 语句
     */
    public static String matchByElementIdIn(final String label, final String paramName) {
        return MATCH_NODE_PREFIX + quoteLabel(label) + WHERE_ELEMENT_ID + "IN $" + paramName + RETURN_NODE;
    }

    /**
     * 批量创建节点
     * <p>
     * 每行属性整体覆盖到新节点，返回各节点的图库标识，顺序与入参一致。
     * </p>
     *
     * @param label 节点标签
     * @return Cypher 语句
     */
    public static String createNodes(final String label) {
        return UNWIND_ROWS + "CREATE (n:" + quoteLabel(label) + ") SET n = row" + RETURN_ELEMENT_ID;
    }

    /**
     * 批量合并节点
     * <p>
     * 按业务主键 MERGE：已存在则增量更新属性，不存在则创建，
     * 避免重复执行造成重复节点。
     * </p>
     *
     * @param label            节点标签
     * @param businessProperty 业务主键属性名
     * @return Cypher 语句
     */
    public static String mergeNodes(final String label, final String businessProperty) {
        final String quotedProperty = quoteProperty(businessProperty);
        return UNWIND_ROWS + "MERGE (n:" + quoteLabel(label)
            + " {" + quotedProperty + ": row." + quotedProperty + "}) SET n += row" + RETURN_ELEMENT_ID;
    }

    /**
     * 批量创建关系
     * <p>
     * 按起止节点的图库标识定位两端节点后建立关系。每行结构为
     * {@code {startId, endId, props}}，其中 props 为关系属性映射。
     * </p>
     *
     * @param type 关系类型
     * @return Cypher 语句
     */
    public static String createRelationships(final String type) {
        return UNWIND_ROWS
            + "MATCH (s) WHERE elementId(s) = row." + ROW_START_ID
            + " MATCH (t) WHERE elementId(t) = row." + ROW_END_ID
            + " CREATE (s)-[r:" + quoteType(type) + "]->(t)"
            + " SET r = row." + ROW_PROPERTIES + " RETURN elementId(r)";
    }

    /**
     * 按图库标识集合删除关系
     *
     * @param type      关系类型
     * @param paramName 绑定参数名
     * @return Cypher 语句
     */
    public static String deleteRelationshipByElementIdIn(final String type, final String paramName) {
        return "MATCH ()-[r:" + quoteType(type) + "]->() WHERE elementId(r) IN $" + paramName + " DELETE r";
    }

    /**
     * 按图库标识集合删除节点
     * <p>
     * 使用 DETACH DELETE，连带删除节点上的关系，避免因残留关系导致删除失败。
     * </p>
     *
     * @param label     节点标签
     * @param paramName 绑定参数名
     * @return Cypher 语句
     */
    public static String detachDeleteByElementIdIn(final String label, final String paramName) {
        return MATCH_NODE_PREFIX + quoteLabel(label) + WHERE_ELEMENT_ID + "IN $" + paramName + " DETACH DELETE n";
    }

    /**
     * 按属性值集合删除节点
     *
     * @param label     节点标签
     * @param property  属性名
     * @param paramName 绑定参数名
     * @return Cypher 语句
     */
    public static String detachDeleteByPropertyIn(final String label, final String property, final String paramName) {
        return MATCH_NODE_PREFIX + quoteLabel(label) + WHERE_NODE_PROPERTY + quoteProperty(property)
            + " IN $" + paramName + " DETACH DELETE n";
    }

    /**
     * 校验并反引号包裹节点标签
     *
     * @param label 节点标签
     * @return 反引号包裹后的标签
     * @throws ServiceException 标签不合法时抛出
     */
    public static String quoteLabel(final String label) {
        return IdentifierUtil.quote(label, IdentifierUtil.CATEGORY_LABEL);
    }

    /**
     * 校验并反引号包裹关系类型
     *
     * @param type 关系类型
     * @return 反引号包裹后的关系类型
     * @throws ServiceException 关系类型不合法时抛出
     */
    public static String quoteType(final String type) {
        return IdentifierUtil.quote(type, IdentifierUtil.CATEGORY_RELATIONSHIP_TYPE);
    }

    /**
     * 校验并反引号包裹属性名
     *
     * @param property 属性名
     * @return 反引号包裹后的属性名
     * @throws ServiceException 属性名不合法时抛出
     */
    public static String quoteProperty(final String property) {
        return IdentifierUtil.quote(property, IdentifierUtil.CATEGORY_PROPERTY);
    }
}
