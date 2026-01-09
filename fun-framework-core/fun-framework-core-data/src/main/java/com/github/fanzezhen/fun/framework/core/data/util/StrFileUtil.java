package com.github.fanzezhen.fun.framework.core.data.util;

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
 * @author fanzezhen
 */
@Slf4j
@SuppressWarnings("unused")
public class StrFileUtil {
    private StrFileUtil() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Class<T> classType, Object value) {
        try {
            T result = classType.getDeclaredConstructor().newInstance();
            if (value == null) {
                return result;
            }
            if (value instanceof String) {
                String valueString = String.valueOf(value);
                if (classType == Date.class) {
                    result = (T) DateUtil.parse(valueString);
                } else if (classType == Long.class) {
                    result = (T) Long.valueOf(valueString);
                }
            }
            return result;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            log.warn("类型转化失败", e);
        }
        return null;
    }

    /**
     * 按行读取文件并替换该行中的指定字符串，将结果写入targetPath
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
     * 按行读取文件并替换该行中的指定字符串，将结果写入targetFilePath
     */
    private static void replaceLineInFile(File file, String targetFilePath, CharSequence target, CharSequence replacement) {
        if (!file.isFile()) {
            return;
        }
        List<String> list = cn.hutool.core.io.FileUtil.readUtf8Lines(file);
        cn.hutool.core.io.FileUtil.writeUtf8Lines(list.stream().map(s -> s.replace(target, replacement)).toList(), new File(targetFilePath));
    }

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
