package com.github.fanzezhen.fun.framework.core.model.util;

import com.sun.management.OperatingSystemMXBean;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务器信息工具类
 * <p>
 * 提供服务器资源使用情况的查询功能，包括内存使用率和磁盘使用率。
 * <p>
 * <b>注意：</b>磁盘扫描方法仅支持Windows系统（A-Z盘符遍历）
 */
public class ServerInfoUtil {
    /**
     * 工具类不允许实例化
     */
    private ServerInfoUtil() {
    }

    /**
     * 获取内存使用率信息
     *
     * @return 内存使用率字符串，格式如 "内存已使用:75%"
     */
    public static String getMemoryMsg() {
        OperatingSystemMXBean operatingSystem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        // 总的物理内存+虚拟内存
        long totalSwapSpaceSize = operatingSystem.getTotalSwapSpaceSize();
        // 剩余的物理内存
        long freePhysicalMemorySize = operatingSystem.getFreeMemorySize();
        double compare = (1 - freePhysicalMemorySize * 1.0 / totalSwapSpaceSize) * 100;
        return "内存已使用:" + (int) compare + "%";
    }

    /**
     * 获取磁盘使用率信息列表
     * <p>
     * <b>注意：</b>仅支持Windows系统（通过A-Z盘符遍历）
     * </p>
     *
     * @return 磁盘使用率字符串列表，格式如 "C:盘  已使用 60%"
     */
    public static List<String> getDiskMsgList() {
        char firstLetter = 'A';
        char lastLetter = 'Z';
        // 操作系统
        List<String> list = new ArrayList<>();
        for (char c = firstLetter; c <= lastLetter; c++) {
            String dirName = c + ":/";
            File win = new File(dirName);
            if (win.exists()) {
                long total = win.getTotalSpace();
                long free = win.getFreeSpace();
                double compare = (1 - free * 1.0 / total) * 100;
                String str = c + ":盘  已使用 " + (int) compare + "%";
                list.add(str);
            }
        }
        return list;
    }

}
