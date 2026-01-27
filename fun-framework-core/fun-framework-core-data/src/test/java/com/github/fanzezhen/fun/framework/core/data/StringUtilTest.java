package com.github.fanzezhen.fun.framework.core.data;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.data.util.StringUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilTest {

    @Test
    void testRemoveFirstPrefix() {
        assertEquals("world", StringUtil.removeFirstPrefix("helloworld", "hello"));
        assertEquals("world", StringUtil.removeFirstPrefix("helloworld", "hello", "hi"));
        assertEquals("helloworld", StringUtil.removeFirstPrefix("helloworld", "test"));
        assertEquals("World", StringUtil.removeFirstPrefix("HelloWorld", true, "hello"));
    }

    @Test
    void testRemoveMaxPrefix() {
        assertEquals(CharSequenceUtil.EMPTY, StringUtil.removeMaxPrefix("helloworld", "hello", "helloworld"));
        assertEquals(CharSequenceUtil.EMPTY, StringUtil.removeMaxPrefix("helloworld", "h", "hello", "helloworld"));
    }

    @Test
    void testRemoveFirstSuffix() {
        assertEquals("hello", StringUtil.removeFirstSuffix("helloworld", "world"));
        assertEquals("hello", StringUtil.removeFirstSuffix("helloworld", "world", "test"));
        assertEquals("helloworld", StringUtil.removeFirstSuffix("helloworld", "test"));
        String string = StringUtil.removeFirstSuffix("HelloWorld", true, "world");
        assertEquals("hello", CharSequenceUtil.toLowerCase(string));
    }

    @Test
    void testRemoveMaxSuffix() {
        assertEquals("hello", StringUtil.removeMaxSuffix("helloworld", "world", "ld"));
        assertEquals("hello", StringUtil.removeMaxSuffix("helloworld", "d", "ld", "world"));
    }
}
