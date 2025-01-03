package com.github.fanzezhen.fun.framework.core.util;

import cn.hutool.core.collection.CollUtil;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Collection;
import java.util.Comparator;

/**
 * @author zezhen.fan
 * @date 2022/8/11
 * Desc:
 */
public class SortUtil {
    /**
     * 冒泡排序
     * 时间复杂度：o(n^2)
     * 空间复杂度：o(1)
     * 稳定性：稳定
     * 优点：简单，易实现，不需要额外空间
     * 缺点：效率低
     *
     * @param arr 待排序数组
     * @return 排序后数组
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
     * 快速排序：
     * 快速排序算法能够快速排序列表或查询。它基于分割交换排序的原则，这种类型的算法占用空间较小，它将待排序列表分为三个主要部分：
     * 小于Pivot的元素
     * 枢轴元素Pivot（选定的比较值）
     * 大于Pivot的元素
     * 时间复杂度：o(nlogn)
     * 空间复杂度：o(logn)
     * 稳定性：不稳定（在排序之前有两个数相等，但是在排序结束之后，它们两个有可能改变顺序，这就是说明该排序算法具有不稳定性。）
     * 优点：排序速度最快
     *
     * @param arr 待排序数组
     */
    public static void quicksort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        quicksort(arr, 0, arr.length - 1);
    }

    public static void quicksort(int[] arr, int left, int right) {
        if (left >= right) {
            return;
        }
        int base = arr[left];
        int i = left, j = right;
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

    public static <T> T getFirstByOrder(Collection<T> beans) {
        if (CollUtil.isEmpty(beans)) {
            return null;
        }
        return beans.stream().min(Comparator.comparingInt(bean -> {
            if (bean instanceof Ordered) {
                return ((Ordered) bean).getOrder();
            }
            Class<?> beanClass = bean.getClass();
            Order order = beanClass.getAnnotation(Order.class);
            if (order != null) {
                return order.value();
            }
            return Integer.MAX_VALUE;
        })).get();
    }

}
