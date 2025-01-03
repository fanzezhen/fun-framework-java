package com.github.fanzezhen.fun.framework.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 子集
 * @author zezhen.fan
 * @date 2023/8/7
 */
@Slf4j
@Disabled
class SubsetTest {

    @Test
    @Disabled
     void testSubset() {
        List<Integer> nums = new ArrayList<>();
        nums.add(1);
        nums.add(2);
        nums.add(3);
        nums.add(4);
        nums.add(5);
        nums.add(6);
        System.out.println(nums.stream().mapToInt(Integer::intValue).sum());
        List<List<Integer>> subsets = getSubsets(nums);
        List<List<Integer>> collect = subsets.stream().filter(l -> l.size() == 3).sorted(Comparator.comparingInt(l -> l.stream().mapToInt(Integer::intValue).sum())).toList();
        System.out.println(collect.size());
        for (List<Integer> subset : collect) {
            System.out.println(subset);
        }
        Assertions.assertTrue(true);
    }

    public static List<List<Integer>> getSubsets(List<Integer> nums) {
        List<List<Integer>> subsets = new ArrayList<>();
        generateSubsets(nums, 0, new ArrayList<>(), subsets);
        return subsets;
    }

    public static void generateSubsets(List<Integer> nums, int index, List<Integer> current, List<List<Integer>> subsets) {
        subsets.add(new ArrayList<>(current));
        for (int i = index; i < nums.size(); i++) {
            current.add(nums.get(i));
            generateSubsets(nums, i + 1, current, subsets);
            current.remove(current.size() - 1);
        }
    }
}
