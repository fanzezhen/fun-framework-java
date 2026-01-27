package com.github.fanzezhen.fun.framework.core.data;

import com.github.fanzezhen.fun.framework.core.data.util.ServerInfoUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerInfoUtilTest {

    @Test
    void testGetMemoryMsg() {
        String memoryMsg = ServerInfoUtil.getMemoryMsg();
        assertNotNull(memoryMsg);
        assertTrue(memoryMsg.contains("内存已使用"));
        assertTrue(memoryMsg.contains("%"));
    }

    @Test
    void testGetDiskMsgList() {
        List<String> diskMsgList = ServerInfoUtil.getDiskMsgList();
        assertNotNull(diskMsgList);
        // 不一定所有盘符都存在，所以不检查大小，只检查是否为有效格式
        for (String msg : diskMsgList) {
            assertTrue(msg.contains("盘"));
            assertTrue(msg.contains("已使用"));
            assertTrue(msg.contains("%"));
        }
    }
}
