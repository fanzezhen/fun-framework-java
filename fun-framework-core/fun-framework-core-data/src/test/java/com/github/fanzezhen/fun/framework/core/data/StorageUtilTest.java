package com.github.fanzezhen.fun.framework.core.data;

import com.github.fanzezhen.fun.framework.core.data.util.StorageUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

 class StorageUtilTest {
    
    @Test
    void testDisplayBytes() {
        assertEquals("1023B", StorageUtil.displayBytes(1023L));
        assertEquals("1K", StorageUtil.displayBytes(1024L));
        assertEquals("2K", StorageUtil.displayBytes(2048L));
        assertEquals("1M", StorageUtil.displayBytes(1024L * 1024));
        assertEquals("1G", StorageUtil.displayBytes(1024L * 1024 * 1024));
        assertEquals("1.5K", StorageUtil.displayBytes(1536L));
        assertNull(StorageUtil.displayBytes(null));
    }
    
    @Test
    void testGetMultiplier() {
        assertEquals(1L, StorageUtil.getMultiplier("b"));
        assertEquals(1024L, StorageUtil.getMultiplier("k"));
        assertEquals(1024L * 1024, StorageUtil.getMultiplier("m"));
        assertEquals(1024L * 1024 * 1024, StorageUtil.getMultiplier("g"));
        assertEquals(1024L * 1024 * 1024 * 1024, StorageUtil.getMultiplier("t"));
        
        assertThrows(IllegalArgumentException.class, () -> StorageUtil.getMultiplier("invalid"));
    }
    
    @Test
    void testConvertToBytes() {
        assertEquals(Long.valueOf(1024), StorageUtil.convertToBytes("1K"));
        assertEquals(Long.valueOf(2048), StorageUtil.convertToBytes("2k"));
        assertEquals(Long.valueOf(1024 * 1024), StorageUtil.convertToBytes("1M"));
        assertEquals(Long.valueOf(1024 * 1024 * 1024), StorageUtil.convertToBytes("1G"));
        
        assertThrows(IllegalArgumentException.class, () -> StorageUtil.convertToBytes("invalid"));
    }
    
    @Test
    void testTruncateStringForBytes() {
        String longString = "这是一个很长的字符串测试截断功能";
        String truncated = StorageUtil.truncateStringForBytes(longString, 20, false);
        assertNotNull(truncated);
        assertTrue(truncated.length() <= longString.length());
        
        String reversed = StorageUtil.truncateStringForBytes(longString, 20, true);
        assertNotNull(reversed);
    }
    
    @Test
    void testTruncateStringForChars() {
        String longString = "这是一个很长的字符串测试截断功能";
        String truncated = StorageUtil.truncateStringForChars(longString, 10, '.', false);
        assertEquals(10, truncated.length());
        assertTrue(truncated.endsWith("."));
        
        String reversed = StorageUtil.truncateStringForChars(longString, 10, '.', true);
        assertEquals(10, reversed.length());
        assertTrue(reversed.startsWith("."));
    }
}
