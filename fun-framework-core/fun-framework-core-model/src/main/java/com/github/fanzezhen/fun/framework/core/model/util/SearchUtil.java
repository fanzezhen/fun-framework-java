package com.github.fanzezhen.fun.framework.core.model.util;

/**
 * 搜索工具类
 * <p>
 * 提供常用的搜索算法实现，如二分查找等。
 * </p>
 */
public class SearchUtil {
    /**
     * 工具类不允许实例化
     */
    private SearchUtil() {
    }

    /**
     * 二分查找算法
     * <p>
     * <b>前置条件：</b>数组必须已按升序排序，否则结果不可预测
     * <br><b>时间复杂度：</b>O(log n)
     * </p>
     *
     * @param arr 已排序的整数数组
     * @param k   待查找的目标值
     * @return 目标值在数组中的索引，未找到返回 -1
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
