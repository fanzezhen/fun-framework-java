package com.github.fanzezhen.fun.framework.data.graph.base.template;

import com.github.fanzezhen.fun.framework.core.model.enums.FunCoreDataExceptionEnum;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Cypher 语句构建器测试
 */
@DisplayName("Cypher 语句构建器")
class CypherBuilderTest {

    @Test
    @DisplayName("按属性匹配：属性值走参数绑定，标签与属性名反引号包裹")
    void testMatchByProperty_ShouldBindValueAndQuoteNames() {
        final String cypher = CypherBuilder.matchByProperty("Person", "name", CypherBuilder.PARAM_VALUE);

        assertEquals("MATCH (n:`Person`) WHERE n.`name` = $value RETURN n", cypher);
    }

    @Test
    @DisplayName("按属性多值匹配：使用 IN 并绑定集合参数")
    void testMatchByPropertyIn_ShouldUseInOperator() {
        final String cypher = CypherBuilder.matchByPropertyIn("Person", "name", CypherBuilder.PARAM_VALUES);

        assertEquals("MATCH (n:`Person`) WHERE n.`name` IN $values RETURN n", cypher);
    }

    @Test
    @DisplayName("按图库标识匹配：使用 elementId 函数")
    void testMatchByElementId_ShouldUseElementIdFunction() {
        assertAll(
            () -> assertEquals("MATCH (n:`Person`) WHERE elementId(n) = $id RETURN n",
                CypherBuilder.matchByElementId("Person", CypherBuilder.PARAM_ID)),
            () -> assertEquals("MATCH (n:`Person`) WHERE elementId(n) IN $ids RETURN n",
                CypherBuilder.matchByElementIdIn("Person", CypherBuilder.PARAM_IDS))
        );
    }

    @Test
    @DisplayName("批量创建节点：UNWIND 展开行并回传图库标识")
    void testCreateNodes_ShouldUnwindRowsAndReturnElementId() {
        final String cypher = CypherBuilder.createNodes("Person");

        assertEquals("UNWIND $rows AS row CREATE (n:`Person`) SET n = row RETURN elementId(n)", cypher);
    }

    @Test
    @DisplayName("批量合并节点：按业务主键 MERGE 并增量更新属性")
    void testMergeNodes_ShouldMergeOnBusinessKey() {
        final String cypher = CypherBuilder.mergeNodes("Person", "person_id");

        assertEquals("UNWIND $rows AS row MERGE (n:`Person` {`person_id`: row.`person_id`}) "
            + "SET n += row RETURN elementId(n)", cypher);
    }

    @Test
    @DisplayName("删除节点：DETACH DELETE 同时清理关联关系")
    void testDetachDelete_ShouldRemoveRelationshipsToo() {
        assertAll(
            () -> assertEquals("MATCH (n:`Person`) WHERE elementId(n) IN $ids DETACH DELETE n",
                CypherBuilder.detachDeleteByElementIdIn("Person", CypherBuilder.PARAM_IDS)),
            () -> assertEquals("MATCH (n:`Person`) WHERE n.`person_id` IN $ids DETACH DELETE n",
                CypherBuilder.detachDeleteByPropertyIn("Person", "person_id", CypherBuilder.PARAM_IDS))
        );
    }

    @Test
    @DisplayName("批量创建关系：按起止节点标识定位两端后建关系")
    void testCreateRelationships_ShouldMatchBothEndpointsByElementId() {
        final String cypher = CypherBuilder.createRelationships("KNOWS");

        assertEquals("UNWIND $rows AS row"
            + " MATCH (s) WHERE elementId(s) = row.startId"
            + " MATCH (t) WHERE elementId(t) = row.endId"
            + " CREATE (s)-[r:`KNOWS`]->(t)"
            + " SET r = row.props RETURN elementId(r)", cypher);
    }

    @Test
    @DisplayName("删除关系：只删关系不动两端节点")
    void testDeleteRelationshipByElementIdIn_ShouldKeepEndpointNodes() {
        final String cypher = CypherBuilder.deleteRelationshipByElementIdIn("KNOWS", CypherBuilder.PARAM_IDS);

        assertEquals("MATCH ()-[r:`KNOWS`]->() WHERE elementId(r) IN $ids DELETE r", cypher);
    }

    @Test
    @DisplayName("注入防护：非法关系类型一律拒绝")
    void testValidateName_IllegalRelationshipType_ShouldThrowServiceException() {
        assertThrows(ServiceException.class,
            () -> CypherBuilder.createRelationships("KNOWS`]->() DETACH DELETE s //"));
    }

    @ParameterizedTest
    @DisplayName("注入防护：非法标签一律拒绝，不进入语句拼接")
    @ValueSource(strings = {
        "Person`) DETACH DELETE n //",
        "Person WHERE 1=1",
        "Person-Node",
        "Person;DROP",
        "1Person",
        "Person Name",
        "",
        " ",
    })
    void testValidateName_IllegalLabel_ShouldThrowServiceException(final String illegalLabel) {
        final ServiceException exception = assertThrows(ServiceException.class,
            () -> CypherBuilder.matchByProperty(illegalLabel, "name", CypherBuilder.PARAM_VALUE));

        assertEquals(FunCoreDataExceptionEnum.ILLEGAL_IDENTIFIER.getCode(), exception.getCode());
    }

    @ParameterizedTest
    @DisplayName("注入防护：非法属性名一律拒绝")
    @ValueSource(strings = {
        "name` = 1 OR true //",
        "name;",
        "na me",
        "2name",
    })
    void testValidateName_IllegalProperty_ShouldThrowServiceException(final String illegalProperty) {
        assertThrows(ServiceException.class,
            () -> CypherBuilder.matchByProperty("Person", illegalProperty, CypherBuilder.PARAM_VALUE));
    }

    @ParameterizedTest
    @DisplayName("合法名称：字母、数字、下划线组合均可通过")
    @ValueSource(strings = {"Person", "person_node", "_Internal", "Node2", "A"})
    void testValidateName_LegalName_ShouldPass(final String legalName) {
        assertEquals("MATCH (n:`" + legalName + "`) WHERE elementId(n) = $id RETURN n",
            CypherBuilder.matchByElementId(legalName, CypherBuilder.PARAM_ID));
    }

    @Test
    @DisplayName("null 名称按非法处理")
    void testValidateName_NullName_ShouldThrowServiceException() {
        assertThrows(ServiceException.class,
            () -> CypherBuilder.matchByElementId(null, CypherBuilder.PARAM_ID));
    }
}
