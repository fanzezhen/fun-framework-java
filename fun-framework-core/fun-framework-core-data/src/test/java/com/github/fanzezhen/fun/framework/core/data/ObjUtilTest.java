package com.github.fanzezhen.fun.framework.core.data;

import com.github.fanzezhen.fun.framework.core.data.util.ObjUtil;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("unchecked")
class ObjUtilTest {

    @Test
    void testEmpty() {
        List<String> emptyList = ObjUtil.empty(List.class);
        assertNotNull(emptyList);
        assertTrue(emptyList.isEmpty());

        Map<String, Object> emptyMap = ObjUtil.empty(Map.class);
        assertNotNull(emptyMap);
        assertTrue(emptyMap.isEmpty());
        
        String emptyStr = ObjUtil.empty(String.class);
        assertEquals("", emptyStr);

        Object emptyObj = ObjUtil.empty(Object.class);
        assertNull(emptyObj);
    }

    @Test
    void testIsNativeClass() {
        assertTrue(ObjUtil.isNativeClass(String.class));
        assertTrue(ObjUtil.isNativeClass(Integer.class));
        assertTrue(ObjUtil.isNativeClass(Long.class));
        assertFalse(ObjUtil.isNativeClass(this.getClass()));
    }

    @Data
    static class TestClass {
        private List<String> stringList;
        private String stringValue;
    }

    @Test
    void testResolveByField() throws NoSuchFieldException {
        Field listField = TestClass.class.getDeclaredField("stringList");
        Field stringField = TestClass.class.getDeclaredField("stringValue");

        // 测试List类型的字段解析
        Object listResult = ObjUtil.resolveByField(listField, "[\"item1\", \"item2\"]");
        assertNotNull(listResult);
        assertInstanceOf(List.class, listResult);

        // 测试String类型的字段解析
        Object stringResult = ObjUtil.resolveByField(stringField, "test value");
        assertEquals("test value", stringResult);
    }
}
