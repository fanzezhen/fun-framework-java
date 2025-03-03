package com.github.fanzezhen.fun.framework.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author zezhen.fan
 * @date 2023/8/7
 */
@Slf4j
@Disabled
class QuestionTest {

    /**
     * 题目描述
     * 给定一个由多个命令字组成的命令字符串:1、字符串长度小于等于127字节，只包含大小写字母，数字下划线和偶数个双引号;
     * 2、命令字之间以一个或多个下划线_进行分割;3、可以通过两个双引号””来标识包含下划线_的命令字或空命令字 (仅包含两个双引号的命令字) ，双引号不会在命令字内部出现;
     * 请对指定索引的敏感字段进行加密，替换为****** (6个*)并删除命令字前后多余的下划线_。
     * 如果无法找到指定索引的命令字，输出字符串ERROR。
     * 输入描述
     * 输入为两行，第一行为命令字索K (从0开始)，第二行为命令字符串S。
     * 输出描述
     * 输出处理后的命令字符串，如果无法找到指定索引的命令字输出字符串ERROR
     * 用
     * 输入：
     * 1
     * password_a12345678_timeout_100
     * 输出
     * password ****** timeout_100
     */
    @Test
    @Disabled("演示用")
    void testEncrypt() {
        Scanner in = new Scanner(System.in);
        String numStr = in.nextLine();
        String str = in.nextLine();
        int num = Integer.parseInt(numStr);
        System.out.println(num);
        System.out.println(str);
        List<String> list = new ArrayList<>();
        int len = str.length();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (str.charAt(i) == '_' && stringBuilder.length() > 0) {
                list.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
            } else if (str.charAt(i) != '_') {
                stringBuilder.append(str.charAt(i));
            }
        }
        if (stringBuilder.length() > 0) {
            list.add(stringBuilder.toString());
        }
        int size = list.size();
        if (size <= num) {
            System.out.println("error");
            return;
        }
        list.set(num, "******");
        System.out.println(String.join("_", list));
        Assertions.assertTrue(true);
    }

    /**
     * 存在一种虚拟IPv4地址Q，由4小节组成，每节的范围为0~255，以#号间隔，虚IP4地址可以转换为一个32位的整数，例如
     * 。128#0#255#255，转换为32位整数的结果为2147549183( 0x8000FFFF)
     * 。1#0#0#0，转换为32位整数的结果为16777216( 0x01000000)现以字符串形式给出一个虚拟IPv4地址，每一节范围分别为(1~128)#(0-255).(0-255).(0-255)
     * 要求每个IPv4地址只能对应到唯一的整数上。
     * 如果是非法IPv4Q，返invalid IP
     */
    @Test
    @Disabled("演示用")
    void testIpv4ToLong() {
        String ipv4 = "128.0.255.255";
        try {
            Integer[] intArr = Arrays.stream(ipv4.split("\\.")).map(Integer::parseInt).toArray(Integer[]::new);
            int ip1 = intArr[0];
            int ip2 = intArr[1];
            int ip3 = intArr[2];
            int ip4 = intArr[3];
            if (ip1 < 0 || ip1 > 128 || ip2 < 0 || ip2 > 255 || ip3 < 0 || ip3 > 255 || ip4 < 0 || ip4 > 255) {
                System.out.println("invalid IP");
            }
            String ipForHex = getHexStr(ip1) + getHexStr(ip2) + getHexStr(ip3) + getHexStr(ip4);
            System.out.println(Long.parseLong(ipForHex, 16));
        } catch (Exception e) {
            System.out.println("invalid IP");
        }
    }

    public static String getHexStr(int i) {
        String hexString = Integer.toHexString(i);
        return hexString.length() < 2 ? ("0" + hexString) : hexString;
    }
}
