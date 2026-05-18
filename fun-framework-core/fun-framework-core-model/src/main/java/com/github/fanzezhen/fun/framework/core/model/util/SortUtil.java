package com.github.fanzezhen.fun.framework.core.model.util;

/**
 * 排序工具类
 * <p>
 * 提供常用的排序算法实现，如冒泡排序、快速排序等。
 * </p>
 */
public class SortUtil {
    /**
     * 工具类不允许实例化
     */
    private SortUtil() {
    }

    /**
     * 冒泡排序（升序）
     * <p>
     * <b>稳定性：</b>稳定
     * <br><b>时间复杂度：</b>O(n²)，最优O(n)（已排序）
     * <br><b>空间复杂度：</b>O(1)
     * </p>
     *
     * @param arr 待排序数组
     * @return 排序后的数组（原地排序）
     */
    public static int[] bubbleSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return arr;
        }
        int temp;
        boolean flag;
        for (int i = 0; i < arr.length - 1; i++) {
            flag = false;
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    flag = true;
                }
            }
            if (!flag) {
                break;
            }
        }
        return arr;
    }

    /**
     * 快速排序（升序）
     * <p>
     * 基于分治思想的高效排序算法，通过选择基准元素将数组分为三部分：
     * - 小于基准的元素
     * - 基准元素
     * - 大于基准的元素
     * </p>
     * <p>
     * <b>时间复杂度：</b>平均O(n log n)，最坏O(n²)（已排序或逆序）
     * <br><b>空间复杂度：</b>O(log n)（递归栈）
     * <br><b>稳定性：</b>不稳定
     * <br><b>优点：</b>平均情况下排序速度最快
     * </p>
     *
     * @param arr 待排序数组
     */
    public static void quicksort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        quicksort(arr, 0, arr.length - 1);
    }

    /**
     * 快速排序递归实现
     *
     * @param arr   待排序数组
     * @param left  左边界索引
     * @param right 右边界索引
     */
    public static void quicksort(int[] arr, int left, int right) {
        if (left >= right) {
            return;
        }
        int base = arr[left];
        int i = left;
        int j = right;
        while (i < j) {
            while (arr[j] > base && i < j) {
                j--;
            }
            while (arr[i] <= base && i < j) {
                i++;
            }
            if (i < j) {
                arr[i] = arr[i] ^ arr[j];
                arr[j] = arr[i] ^ arr[j];
                arr[i] = arr[i] ^ arr[j];
            }
        }
        // 将基准数放到中间的位置（基准数归位）
        arr[left] = arr[i];
        arr[i] = base;
        // 递归，继续向基准的左右两边执行和上面同样的操作
        // i的索引处为上面已确定好的基准值的位置，无需再处理
        quicksort(arr, left, i - 1);
        quicksort(arr, i + 1, right);
    }

}
