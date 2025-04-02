package com.github.fanzezhen.fun.framework.core.data;

/**
 * @author fanzezhen
 */
public class SearchUtil {
    private SearchUtil() {
    }

    /**
     * 二分查找
     *
     * @param arr 查找的数组
     * @param k   需要查找的元素
     * @return 要查找的元素在数组中的位置（-1即数组中不存在该元素）
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
