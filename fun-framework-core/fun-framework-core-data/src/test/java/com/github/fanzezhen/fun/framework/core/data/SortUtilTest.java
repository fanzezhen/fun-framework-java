package com.github.fanzezhen.fun.framework.core.data;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.Arrays;

@Slf4j
@Disabled
class SortUtilTest {
    int[] arr = new int[]{3, 1, 7, 4, 5, 2, 6};
    int[] arr2 = new int[]{3, 1, 7, 4, 5, 3, 6, 2};

    @Test
    @Disabled("学习用")
     void testBubbleSort() {
        SortUtil.bubbleSort(arr);
        System.out.println(Arrays.toString(arr));
        Assertions.assertTrue(true);
    }

    @Test
    @Disabled("学习用")
     void testQuicksort() {
        SortUtil.quicksort(arr, 0, arr.length - 1);
        System.out.println(Arrays.toString(arr));
        SortUtil.quicksort(arr2, 0, arr2.length - 1);
        System.out.println(Arrays.toString(arr2));
        Assertions.assertTrue(true);
    }
}
