package com.github.fanzezhen.fun.framework.proxy.methodhandle;

import com.github.fanzezhen.fun.framework.core.model.mapper.FunObjectMapper;
import com.github.fanzezhen.fun.framework.core.model.mapper.MethodHandleObjectMapper;
import com.github.fanzezhen.fun.framework.proxy.core.ProxyField;
import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link ProxyFieldValueHook} 单元测试。
 * <p>
 * 验证：@ProxyField 注解的 String 字段在映射时被代理装饰，非注解字段原样拷贝。
 * </p>
 *
 * @since 4.1.0
 */
class ProxyFieldValueHookTest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Source {
        private String avatarUrl;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Target {
        @ProxyField
        private String avatarUrl;
        private String name;
    }

    private FunObjectMapper mapper;

    @BeforeEach
    void setUp() throws Exception {
        ProxyFieldValueHook hook = new ProxyFieldValueHook();
        injectProxyHelper(hook, new PrefixProxyHelper());
        mapper = new MethodHandleObjectMapper(List.of(hook));
    }

    @Test
    void testProxyFieldDecorated() {
        Target t = mapper.map(new Source("/logo.png", "张三"), Target.class);
        assertEquals("https://cdn.example.com/logo.png", t.getAvatarUrl());
        assertEquals("张三", t.getName());
    }

    /**
     * 用反射把伪造的 ProxyHelper 注入 hook 的 @Resource 私有字段。
     */
    private static void injectProxyHelper(final ProxyFieldValueHook hook, final ProxyHelper helper) throws Exception {
        Field field = ProxyFieldValueHook.class.getDeclaredField("proxyHelper");
        field.setAccessible(true);
        field.set(hook, helper);
    }

    /**
     * 追加固定前缀的伪造 ProxyHelper，覆写 decorateStr 模拟 URL 代理，绕开 ProxyDecorator。
     */
    static class PrefixProxyHelper extends ProxyHelper {
        @Override
        public String decorateStr(final String s) {
            return "https://cdn.example.com" + s;
        }
    }
}
