package com.github.fanzezhen.fun.framework.core.data;

import com.github.fanzezhen.fun.framework.core.data.util.NumberUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

 class NumberUtilTest {
    
    @Test
    void testNumToChinese() {
        assertEquals("一", NumberUtil.numToChinese("1"));
        assertEquals("二", NumberUtil.numToChinese("2"));
        assertEquals("十", NumberUtil.numToChinese("10"));
        assertEquals("十五", NumberUtil.numToChinese("15"));
        assertEquals("一百二十三", NumberUtil.numToChinese("123"));
        assertEquals("一万零九百零六", NumberUtil.numToChinese("10906"));
    }
    
    @Test
    void testChineseNumberToInteger() {
        assertEquals(Integer.valueOf(1), NumberUtil.chineseNumberToInteger("一"));
        assertEquals(Integer.valueOf(10), NumberUtil.chineseNumberToInteger("十"));
        assertEquals(Integer.valueOf(15), NumberUtil.chineseNumberToInteger("十五"));
        assertEquals(Integer.valueOf(123), NumberUtil.chineseNumberToInteger("一百二十三"));
        assertEquals(Integer.valueOf(100000), NumberUtil.chineseNumberToInteger("十万"));
        assertNull(NumberUtil.chineseNumberToInteger(""));
        assertNull(NumberUtil.chineseNumberToInteger(null));
    }
    
    @Test
    void testToPercent() {
        assertEquals("50%", NumberUtil.toPercent("0.5"));
        assertEquals("12.34%", NumberUtil.toPercent("0.1234"));
        assertEquals("100%", NumberUtil.toPercent("1"));
        assertEquals("123%", NumberUtil.toPercent("1.23"));
        assertEquals("123.45%", NumberUtil.toPercent("1.2345"));
        assertNull(NumberUtil.toPercent(null));
        assertEquals("", NumberUtil.toPercent(""));
    }
}
