package com.github.fanzezhen.fun.framework.core.data;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fanzezhen
 * @date 2023/8/7
 */
@Slf4j
@Disabled
class StudyTest {

    public static void main(String[] args) {
        String str = "The furthest distance in the world. ls not between life and death, But when I stand in front of you, Yet you don't know that I love you";
        System.out.println(Arrays.toString(str.split("[^a-zA-Z]")));
    }

    /**
     * TLV编码是按[Tag Length Value 格式进行编码的，一段码流中的信元用Tag标识,Tag在码流中唯一不重复，Length表示信元Value的长度，Value表示信元的值。
     * 码流以某信元的Tag开头，Tag固定占一个字节，Length固定占两个字节，字节序为小端序.
     * 现给定TLV格式编码的码流，以及需要解码的信元Tag，请输出该信元的Value。输入码流的16进制字符中，不包括小写字母，且要求输出的16进制字符串中也不要包含小写字母，码流字符串的最大长度不超过50000个字节。
     * 用例
     * 输入
     * 31
     * 32 01 00 AE 90 02 00 01 02 30 03 00 AB 32 31 31 02 00 32 33 33 01 00 CC
     * 输出
     * 32 33
     */
    @Test
    @Disabled("学习用")
     void testTLV() {
        String tag = "31";
        String msg = "32 01 00 AE 90 02 00 01 02 30 03 00 AB 32 31 31 02 00 32 33 33 01 00 CC";
        String[] streams = msg.split(" ");
        int i =0;
        while (i<streams.length){
            String currTag = streams[i++];
            String len1 = streams[i++];
            String len2 = streams[i++];
            int len = Integer.valueOf(len2+len1, 16);
            List<String> valList = new ArrayList<>(len);
            for (int j = 0; j < len; j++) {
                valList.add(streams[i++]);
            }
            if (currTag.equals(tag)){
                System.out.println(String.join(" ", valList));
                break;
            }
        }
        Assertions.assertTrue(true);
    }

    /**
     * 依据用户输入的单词前缀，从已输入的英文语句中联想出用户想输入的单词，按字典序输出联想到的单词序列.如果联想不到，请输出用户输入的单词前缀
     * 注意
     * 1.英文单词联想时，区分大小写
     * 2.缩略形式如”don't”，判定为两个单词，”don”和”t”
     * 3.输出的单词序列，不能有重复单词，且只能是英文单词，不能有标点符号
     * 用例
     * 输入 "The furthest distance in the world. ls not between life and death, But when I stand in front of you, Yet you don't know that I love you"
     * 输出 front furthest
     */
    @Test
    @Disabled("学习用")
     void testAssociationEnglish() {
        String msg = "The furthest distance in the world. ls not between life and death, But when I stand in front of you, Yet you don't know that I love you";
        String pre = "f";
        String[] words = msg.split("[^a-zA-Z]");
        TreeSet<String> set = Arrays.stream(words).collect(Collectors.toCollection(TreeSet::new));
        List<String> list = set.stream().filter(s -> s.startsWith(pre)).toList();
        if (list.isEmpty()) {
            System.out.println(pre);
        } else {
            System.out.print(String.join(" ", list));
        }
        Assertions.assertTrue(true);
    }

    /**
     * 一串末加密的字符串str，通过对字符串的每一个字母进行改变来实现加密，加密方式是在每一个字母str[i]偏移特定数组元素a[i]的量，数组a前三位已经赋值 : a[0]=1,a[1]=2,a[2]=4。
     * 当i>=3时，数组元素a[=a[i-1]+a[i-2]+a[i-3]。
     * 例如: 原文 abcde 加密后 bdgkr，其中偏移量分别是1,2.4,7,13。
     * 输入描述
     * 第一行为一个整数n( 1<=n<=1000 )，表示有n测试数据，每组数据包含一行，原文str(只含有小写字母，0<长度<=50)。
     * 输出描述
     * 每组测试数据输出一行，表示字符串的密文
     * 用例：
     * 输入xy，输出ya
     */
    @Test
    @Disabled("学习用")
     void testEncryptOffset() {
        String origin = "xy";
        int length = origin.length();
        long[] arr = new long[length];
        if (length == 0) {
            return;
        }
        if (length > 0) {
            arr[0] = 1;
            if (length > 1) {
                arr[1] = 2;
                if (length > 2) {
                    arr[2] = 4;
                    if (length > 3) {
                        for (int i = 3; i < length; i++) {
                            arr[i] = arr[i - 1] + arr[i - 2] + arr[i - 3];
                        }
                    }
                }
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = (char) ((origin.charAt(i) + arr[i] - 97) % 26 + 97);
            stringBuilder.append(c);
        }
        System.out.println(stringBuilder);
        Assertions.assertTrue(true);
    }

    @Test
    @Disabled("学习用")
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
        Assertions.assertTrue(true);
    }

    public static String getHexStr(int i) {
        String hexString = Integer.toHexString(i);
        return hexString.length() < 2 ? ("0" + hexString) : hexString;
    }

    /**
     * 单词接龙
     */
    @Test
    @Disabled("学习用")
    void testWordsSolitaire() {
        String[] strings = {"word", "dd", "da", "dc", "dword", "d"};
        System.out.println(wordsSolitaire(strings[0], strings, CollUtil.newArrayList(0)));
        System.out.println(wordsSolitaireCupidity(strings[0], strings, CollUtil.newArrayList(0)));
        Assertions.assertTrue(true);
    }


    /**
     * 单词接龙，获取单次最长且序列最小的结果
     */
    public static String wordsSolitaireCupidity(String str, String[] strings, List<Integer> alreadyList) {
        char c = str.charAt(str.length() - 1);
        int ix = -1;
        for (int i = 0; i < strings.length; i++) {
            if (alreadyList.contains(i)) {
                continue;
            }
            if (strings[i].charAt(0) == c) {
                if (ix < 0 || strings[i].length() > strings[ix].length()) {
                    ix = i;
                } else if (strings[i].length() == strings[ix].length()) {
                    for (int j = 0; j < strings[i].length(); j++) {
                        if (strings[i].charAt(j) < strings[ix].charAt(j)) {
                            ix = i;
                            break;
                        }
                    }
                }
            }
        }
        String newStr = "";
        if (ix >= 0) {
            List<Integer> list = new ArrayList<>(alreadyList);
            list.add(ix);
            newStr = wordsSolitaireCupidity(strings[ix], strings, list);
        }
        return str + newStr;
    }

    /**
     * 单词接龙，获取最长结果
     */
    public static String wordsSolitaire(String str, String[] strings, List<Integer> alreadyList) {
        char c = str.charAt(str.length() - 1);
        String newStr = "";
        for (int i = 0; i < strings.length; i++) {
            if (alreadyList.contains(i)) {
                continue;
            }
            if (strings[i].charAt(0) == c) {
                List<Integer> list = new ArrayList<>(alreadyList);
                list.add(i);
                String wordsSolitaire = wordsSolitaire(strings[i], strings, list);
                if (newStr.isEmpty() || wordsSolitaire.length() > newStr.length()) {
                    newStr = wordsSolitaire;
                } else if (wordsSolitaire.length() == newStr.length()) {
                    for (int j = 0; j < wordsSolitaire.length(); j++) {
                        if (wordsSolitaire.charAt(j) < newStr.charAt(j)) {
                            newStr = wordsSolitaire;
                            break;
                        }
                    }
                }
            }
        }
        return str + newStr;
    }
}
