package com.github.fanzezhen.fun.framework.core.model.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * MethodHandle 引擎字段写入 hook 测试。
 * <p>
 * 验证 {@link FieldValueHook} 扩展点等价于 Orika CustomFilter 的字段级装饰能力：
 * 命中字段被装饰，非命中字段原样拷贝。
 * </p>
 *
 * @since 4.1.0
 */
class MethodHandleHookTest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Source {
        private String url;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Target {
        private String url;
        private String name;
    }

    /**
     * 仅对名为 url 的 String 字段追加代理前缀，模拟脱敏/URL 代理。
     */
    static class UrlHook implements FieldValueHook {
        @Override
        public boolean supports(final Class<?> destinationClass, final String fieldName, final Class<?> fieldType) {
            return "url".equals(fieldName) && fieldType == String.class;
        }

        @Override
        public Object apply(final Object value, final Class<?> destinationClass, final String fieldName) {
            return value == null ? null : "https://cdn.example.com" + value;
        }
    }

    @Test
    void testHookDecoratesMatchedField() {
        FunObjectMapper mapper = new MethodHandleObjectMapper(List.of(new UrlHook()));
        Target t = mapper.map(new Source("/logo.png", "张三"), Target.class);
        assertEquals("https://cdn.example.com/logo.png", t.getUrl());
        assertEquals("张三", t.getName());
    }

    @Test
    void testNoHookLeavesFieldUntouched() {
        FunObjectMapper mapper = new MethodHandleObjectMapper();
        Target t = mapper.map(new Source("/logo.png", "张三"), Target.class);
        assertEquals("/logo.png", t.getUrl());
        assertEquals("张三", t.getName());
    }
}
