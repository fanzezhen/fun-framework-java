package com.github.fanzezhen.fun.framework.core.data;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 矩阵工具类
 *
 * @author fanzezhen
 */
@Slf4j
public class MatrixUtil {
    static Random rand = new Random();

    // 添加私有构造函数
    private MatrixUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 使矩阵中的元素变错
     *
     * @param rateOfChange   变错百分比
     * @param originFilePath 原矩阵Excel文件位置
     * @param targetFilePath 错矩阵Excel文件位置
     */
    public static boolean makeWrong(double rateOfChange, String originFilePath, String targetFilePath) {
        // 需要改变的百分比取反，用于判断应该从不变的元素入手还是变错的元素入手
        double antiRateOfChange = 1 - rateOfChange;
        // 判断应该从不变的元素入手还是变错的元素入手，true时是从不变的元素入手
        boolean isAnti = rateOfChange > 0.5;
        // 实际执行中起作用的百分率
        double realRate = isAnti ? antiRateOfChange : rateOfChange;
        ExcelReader reader = ExcelUtil.getReader(FileUtil.file(originFilePath));
        List<List<Object>> readAll = reader.read();
        int rowNum = readAll.size();
        int colNum = readAll.get(0).size();
        int countMatrixPoint = rowNum * colNum;
        double realCount = countMatrixPoint * realRate;
        List<Integer> allMatrixPointList = new ArrayList<>(countMatrixPoint);
        List<Integer> allIdxMatrixPointList = new ArrayList<>(countMatrixPoint);
        List<Integer> dealIdxMatrixPointList = isAnti ? null : new ArrayList<>((int) realCount);
        for (int i = 0; i < countMatrixPoint; i++) {
            allIdxMatrixPointList.add(i);
        }

        int rowNo = 1;
        for (List<Object> colList : readAll) {
            if (colList.size() != colNum) {
                log.warn("矩阵源文件中元素个数与参数不符！行号为{}，改行元素个数为{}", rowNo, colList.size());
                return false;
            }
            colList.forEach(i -> allMatrixPointList.add(Integer.valueOf(String.valueOf(i))));
            rowNo++;
        }

        for (int i = 0, count = countMatrixPoint; i < realCount; i++, count--) {
            int idx = rand.nextInt(count);
            int realIdx = allIdxMatrixPointList.remove(idx);
            if (!isAnti) {
                dealIdxMatrixPointList.add(realIdx);
            }
        }

        List<Integer> realIdxMatrixPointList = isAnti ? allIdxMatrixPointList : dealIdxMatrixPointList;
        if (CollUtil.isEmpty(realIdxMatrixPointList)) {
            log.warn("变错失败，需要改变的元素个数不能为0或等于元素总数！");
            return false;
        }

        realIdxMatrixPointList.forEach(idx -> allMatrixPointList.set(idx, (allMatrixPointList.get(idx) + 1) % 2));

        List<List<Integer>> writeAll = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            List<Integer> row = new ArrayList<>(colNum);
            for (int colIdx = 0; colIdx < colNum; colIdx++) {
                int idx = i * rowNum + colIdx;
                row.add(allMatrixPointList.get(idx));
            }
            writeAll.add(row);
        }

        // 通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter(targetFilePath);
        // 一次性写出内容，强制输出标题
        writer.write(writeAll);
        // 关闭writer，释放内存
        writer.close();
        return true;
    }
}
