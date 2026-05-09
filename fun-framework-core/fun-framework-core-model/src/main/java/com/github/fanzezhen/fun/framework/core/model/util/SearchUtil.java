package com.github.fanzezhen.fun.framework.core.model.util;

/**
 */
public class SearchUtil {
    private SearchUtil() {
    }

    /**
     * 前置条件：arr必须已排序（升序），否则结果不可预测
     * 时间复杂度：O(log n)
     */
    public static int binarySearch(int[] arr, int k) {
        if (arr == null) {
            return -1;
        }
        int end = arr.length;
        if (end == 0) {
            return -1;
        }
        int start = 0;
        while (start <= end) {
            int middle = (start + end) / 2;
            if (arr[middle] > k) {
                end = middle - 1;
            } else if (arr[middle] < k) {
                start = middle + 1;
            } else {
                return middle;
            }
        }
        return -1;
    }
}
