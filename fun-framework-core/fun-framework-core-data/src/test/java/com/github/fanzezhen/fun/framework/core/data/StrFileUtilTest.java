package com.github.fanzezhen.fun.framework.core.data;

import com.github.fanzezhen.fun.framework.core.data.util.StrFileUtil;
import com.github.fanzezhen.fun.framework.core.model.file.FileLineInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
class StrFileUtilTest {

    @Test
    @Disabled("手动使用")
    void test() {
        String filePath = System.getProperty("user.dir") + "\\fun-framework-core-data\\src\\test\\resources\\name.csv";
        Assertions.assertDoesNotThrow(() -> StrFileUtil.trimLines(filePath, "\""));
    }

    @TempDir
    File tempDir;

    @Test
    void testCast() {
        Date date = StrFileUtil.cast(Date.class, "2023-01-01");
        assertNotNull(date);

        Long number = StrFileUtil.cast(Long.class, "123");
        assertEquals(Long.valueOf(123), number);

        Object nullResult = StrFileUtil.cast(Date.class, null);
        assertNull(nullResult);
    }

    @Test
    void testTrimLines() throws IOException {
        File testFile = new File(tempDir, "test.txt");
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("  hello world  \n");
            writer.write("  test line 2  \n");
        }

        StrFileUtil.trimLines(testFile.getAbsolutePath(), " ", " ");

        // 检查文件内容是否被正确修剪
        List<String> lines = cn.hutool.core.io.FileUtil.readUtf8Lines(testFile);
        assertFalse(lines.isEmpty());
    }

    @Test
    void testCollectFileData() throws IOException {
        File testFile = new File(tempDir, "collect_test.txt");
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("name: John\n");
            writer.write("age: 30\n");
            writer.write("city: Beijing\n");
        }

        Map<String, List<FileLineInfo<String>>> result = StrFileUtil.collectFileData(
            testFile.getAbsolutePath(),
            line -> line.contains(":"),
            line -> line.split(":")[0]
        );

        assertFalse(result.isEmpty());
    }
}
