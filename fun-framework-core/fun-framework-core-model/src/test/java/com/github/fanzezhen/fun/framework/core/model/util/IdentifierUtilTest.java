package com.github.fanzezhen.fun.framework.core.model.util;

import com.github.fanzezhen.fun.framework.core.model.enums.FunCoreDataExceptionEnum;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 数据标识符工具测试
 */
@DisplayName("数据标识符工具")
class IdentifierUtilTest {

    /**
     * 合法标识符样本
     */
    private static final List<String> LEGAL_LIST =
        List.of("Person", "person_node", "_Internal", "Node2", "A", "sys_user");

    /**
     * 非法标识符样本，覆盖注入构造、空白与数字开头
     */
    private static final List<String> ILLEGAL_LIST = List.of(
        "Person`) DETACH DELETE n //",
        "sys_user; DROP TABLE x",
        "user WHERE 1=1",
        "user-name",
        "user name",
        "1user",
        "",
        " ");

    @Test
    @DisplayName("合法标识符：字母、数字、下划线组合均通过")
    void testIsLegal_LegalIdentifier_ShouldReturnTrue() {
        assertAll(LEGAL_LIST.stream()
            .map(name -> () -> assertTrue(IdentifierUtil.isLegal(name), "应判为合法：" + name)));
    }

    @Test
    @DisplayName("非法标识符：含空格、符号、以数字开头或为空均拒绝")
    void testIsLegal_IllegalIdentifier_ShouldReturnFalse() {
        assertAll(ILLEGAL_LIST.stream()
            .map(name -> () -> assertFalse(IdentifierUtil.isLegal(name), "应判为非法：" + name)));
    }

    @Test
    @DisplayName("null 按非法处理")
    void testIsLegal_Null_ShouldReturnFalse() {
        assertFalse(IdentifierUtil.isLegal(null));
    }

    @Test
    @DisplayName("引用包裹：合法标识符加反引号")
    void testQuote_LegalIdentifier_ShouldWrapWithBacktick() {
        assertAll(
            () -> assertEquals("`sys_user`", IdentifierUtil.quote("sys_user", IdentifierUtil.CATEGORY_TABLE)),
            () -> assertEquals("`Person`", IdentifierUtil.quote("Person", IdentifierUtil.CATEGORY_LABEL))
        );
    }

    @Test
    @DisplayName("引用包裹：非法标识符一律抛业务异常，不进入语句拼接")
    void testQuote_IllegalIdentifier_ShouldThrowServiceException() {
        assertAll(Stream.concat(
            ILLEGAL_LIST.stream().map(name -> () -> assertThrows(ServiceException.class,
                () -> IdentifierUtil.quote(name, IdentifierUtil.CATEGORY_COLUMN), "应拒绝：" + name)),
            Stream.of(() -> assertThrows(ServiceException.class,
                () -> IdentifierUtil.quote(null, IdentifierUtil.CATEGORY_COLUMN)))));
    }

    @Test
    @DisplayName("异常携带标识符非法错误码")
    void testQuote_IllegalIdentifier_ShouldCarryIllegalIdentifierCode() {
        final ServiceException exception = assertThrows(ServiceException.class,
            () -> IdentifierUtil.quote("user`; DROP", IdentifierUtil.CATEGORY_COLUMN));

        assertEquals(FunCoreDataExceptionEnum.ILLEGAL_IDENTIFIER.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("校验：合法标识符原样返回")
    void testRequireLegal_LegalIdentifier_ShouldReturnOrigin() {
        assertEquals("name", IdentifierUtil.requireLegal("name", IdentifierUtil.CATEGORY_COLUMN));
    }

    @Test
    @DisplayName("校验：异常信息包含标识符类别，便于定位")
    void testRequireLegal_IllegalIdentifier_ShouldCarryCategoryInMessage() {
        final ServiceException exception = assertThrows(ServiceException.class,
            () -> IdentifierUtil.requireLegal("1bad", IdentifierUtil.CATEGORY_LABEL));

        assertTrue(exception.getMessage().contains(IdentifierUtil.CATEGORY_LABEL),
            "异常信息应包含标识符类别，实际：" + exception.getMessage());
    }
}
