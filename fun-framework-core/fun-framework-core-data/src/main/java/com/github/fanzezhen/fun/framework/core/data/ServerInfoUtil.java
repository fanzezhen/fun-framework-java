package com.github.fanzezhen.fun.framework.core.data;

import cn.hutool.core.text.CharSequenceUtil;
import com.sun.management.OperatingSystemMXBean;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fanzezhen
 */
public class ServerInfoUtil {
    private ServerInfoUtil() {
    }

    /**
     * 获取内存使用率
     *
     * @return MemoryMsg
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

    public static String getDiskRatingString() {
        double usableSpace = 0;
        double totalSpace = 0;
        File[] disks = File.listRoots();
        for (File file : disks) {
            usableSpace += file.getUsableSpace();
            totalSpace += file.getTotalSpace();
        }
        if (totalSpace == 0){
            return CharSequenceUtil.EMPTY;
        }
        return new DecimalFormat("0.00").format(usableSpace / totalSpace * 100) + "%";
    }

    public static float getDiskRating() {
        float usableSpace = 0;
        float totalSpace = 0;
        File[] disks = File.listRoots();
        for (File file : disks) {
            usableSpace += file.getUsableSpace();
            totalSpace += file.getTotalSpace();
        }
        if (totalSpace == 0){
            return 0f;
        }
        return usableSpace / totalSpace;
    }

    /**
     * 获取文件系统使用率
     *
     * @return List<String>
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
