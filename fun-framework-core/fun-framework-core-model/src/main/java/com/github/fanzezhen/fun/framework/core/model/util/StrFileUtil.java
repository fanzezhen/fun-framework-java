package com.github.fanzezhen.fun.framework.core.model.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ArrayUtil;
import com.github.fanzezhen.fun.framework.core.model.file.FileContentInfo;
import com.github.fanzezhen.fun.framework.core.model.file.FileLineInfo;
import com.github.fanzezhen.fun.framework.core.model.file.FileLineMatchRule;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * 文件字符串处理工具类
 * <p>
 * 提供文件内容的按行替换、匹配行提取、多规则匹配等功能。
 * 支持对文件或目录进行批量处理。
 * <p>
 * <b>使用场景：</b>日志文件解析、配置文件批量替换、文本数据采集
 */
@Slf4j
@SuppressWarnings("unused")
public class StrFileUtil {
    /**
     * 工具类不允许实例化
     */
    private StrFileUtil() {
    }

    /**
     * 类型转换方法
     * <p>
     * 支持 String 到 Date/Long 的转换，其他类型尝试调用无参构造函数创建实例
     * </p>
     *
     * @param classType 目标类型
     * @param value     原始值
     * @param <T>       目标类型参数
     * @return 转换后的对象，转换失败返回 null
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Class<T> classType, Object value) {
        try {
            if (classType.isInstance(value)) {
                return (T) value;
            }
            if (value == null) {
                return null;
            }
            T result;
            if (value instanceof String) {
                String valueString = String.valueOf(value);
                if (classType == Date.class) {
                    return (T) DateUtil.parse(valueString);
                } else if (classType == Long.class) {
                    return (T) Long.valueOf(valueString);
                }
            }
            return classType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            log.warn("类型转化失败", e);
        }
        return null;
    }

    /**
     * 批量替换文件或目录中所有文件的指定字符串（按行处理）
     * <p>
     * 支持单文件处理和目录递归处理。
     * 处理后的结果写入 targetPath 指定的位置。
     * </p>
     *
     * @param originPath  源文件或目录路径
     * @param targetPath  目标文件或目录路径
     * @param target      待替换的字符串
     * @param replacement 替换为的字符串
     */
    public static void replaceLineInFile(String originPath, String targetPath, CharSequence target, CharSequence replacement) {
        File file = new File(originPath);
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            replaceLineInFile(file, targetPath, target, replacement);
        }
        File[] files = file.listFiles();
        if (ArrayUtil.isEmpty(files)) {
            return;
        }
        if (!targetPath.endsWith(File.separator) && !targetPath.endsWith(StrPool.BACKSLASH)) {
            targetPath += File.separator;
        }
        for (File listFile : files) {
            replaceLineInFile(listFile, targetPath + listFile.getName(), target, replacement);
        }
    }

    /**
     * 替换单个文件中的指定字符串（按行处理）
     *
     * @param file           源文件
     * @param targetFilePath 目标文件路径
     * @param target         待替换的字符串
     * @param replacement    替换为的字符串
     */
    private static void replaceLineInFile(File file, String targetFilePath, CharSequence target, CharSequence replacement) {
        if (!file.isFile()) {
            return;
        }
        List<String> list = cn.hutool.core.io.FileUtil.readUtf8Lines(file);
        cn.hutool.core.io.FileUtil.writeUtf8Lines(list.stream().map(s -> s.replace(target, replacement)).toList(), new File(targetFilePath));
    }

    /**
     * 去除文件中每行的指定前后缀
     * <p>
     * 读取文件所有行，移除每行首尾的指定字符串，然后写回原文件
     * </p>
     *
     * @param filePath 文件路径
     * @param trims    需要移除的前后缀字符串数组
     */
    public static void trimLines(String filePath, String... trims) {
        List<String> lines = cn.hutool.core.io.FileUtil.readUtf8Lines(filePath);
        List<String> list = lines.stream().map(line -> {
            String updated = line;
            do {
                line = updated;
                for (String trim : trims) {
                    updated = CharSequenceUtil.removeSuffix(CharSequenceUtil.removePrefix(line, trim), trim);
                }
            } while (!Objects.equals(updated, line));
            return line;
        }).toList();
        cn.hutool.core.io.FileUtil.writeUtf8Lines(list, filePath);
    }

    /**
     * 从文件或目录中提取匹配行的数据
     * <p>
     * 支持单文件处理和目录递归处理。
     * 对每一行应用匹配规则，匹配成功的行会提取数据并记录行号。
     * </p>
     *
     * @param filePath        文件或目录路径
     * @param matchPredicate  行匹配规则
     * @param extractFunction 数据提取函数（可为null）
     * @param <T>             提取数据的类型
     * @return 文件路径到匹配行信息列表的映射
     */
    public static <T> Map<String, List<FileLineInfo<T>>> collectFileData(String filePath,
                                                                         Predicate<String> matchPredicate,
                                                                         Function<String, T> extractFunction) {
        Map<String, List<FileLineInfo<T>>> fileLineInfoListMap = new HashMap<>();
        List<FileLineInfo<T>> fileLineInfoList = new ArrayList<>();
        if (FileUtil.isDirectory(filePath)) {
            for (String filename : FileUtil.listFileNames(filePath)) {
                fileLineInfoListMap.putAll(collectFileData(filePath + File.separator + filename, matchPredicate, extractFunction));
            }
        } else if (FileUtil.isFile(filePath)) {
            List<String> lines = FileUtil.readUtf8Lines(filePath);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (matchPredicate.test(line)) {
                    FileLineInfo<T> fileLineInfo = new FileLineInfo<>();
                    fileLineInfo.setFilename(filePath);
                    fileLineInfo.setNumber(i + 1);
                    fileLineInfo.setContent(line);
                    if (extractFunction != null) {
                        fileLineInfo.setData(extractFunction.apply(line));
                    }
                    fileLineInfoListMap.computeIfAbsent(filePath, k -> new ArrayList<>()).add(fileLineInfo);
                }
            }
        }
        return fileLineInfoListMap;
    }

    /**
     * 从文件或目录中提取符合多规则匹配的数据
     * <p>
     * 支持对一个文件应用多个匹配规则，提取符合所有规则的数据。
     * 常用于日志文件解析、配置文件分析等场景。
     * </p>
     *
     * @param filePath           文件或目录路径
     * @param lineMatchRuleList  行匹配规则列表
     * @param aliasNameOperator  文件别名生成函数（可为null）
     * @param <T>                提取数据的类型
     * @return 文件内容信息列表
     */
    public static <T> List<FileContentInfo<T>> collectFileData(String filePath,
                                                               List<FileLineMatchRule<T>> lineMatchRuleList,
                                                               UnaryOperator<String> aliasNameOperator) {
        List<FileContentInfo<T>> fileContentInfoList = new ArrayList<>();
        if (FileUtil.isDirectory(filePath)) {
            for (String filename : FileUtil.listFileNames(filePath)) {
                fileContentInfoList.addAll(collectFileData(filePath + File.separator + filename, lineMatchRuleList, aliasNameOperator));
            }
        } else if (FileUtil.isFile(filePath)) {
            T data = null;
            List<FileContentInfo.HitLine> hitLineList = new ArrayList<>();
            List<String> lines = FileUtil.readUtf8Lines(filePath);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                for (FileLineMatchRule<T> lineMatchRule : lineMatchRuleList) {
                    if (lineMatchRule.getPredicate().test(line, data)) {
                        hitLineList.add(new FileContentInfo.HitLine(lineMatchRule.getName(), line, i + 1));
                        data = lineMatchRule.getExtractFunction().apply(line, data);
                    }
                }
            }
            if (data != null) {
                FileContentInfo<T> fileContentInfo = new FileContentInfo<>();
                fileContentInfo.setFilename(filePath);
                if (aliasNameOperator != null) {
                    fileContentInfo.setAliasName(aliasNameOperator.apply(filePath));
                }
                fileContentInfo.setHitLineList(hitLineList);
                fileContentInfo.setData(data);
                fileContentInfoList.add(fileContentInfo);
            }
        }
        return fileContentInfoList;
    }

}
