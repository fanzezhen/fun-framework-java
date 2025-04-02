package com.github.fanzezhen.fun.framework.core.data;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

/**
 * @author fanzezhen
 */
@Slf4j
@SuppressWarnings("unused")
public class FileUtil {
    private FileUtil() {
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

}
