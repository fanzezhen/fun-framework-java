package com.github.fanzezhen.fun.framework.core.thread;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author zezhen.fan
 */
@Slf4j
@Disabled
class StoryTest {

    @Test
    @Disabled("手动调用")
     void testChapters() throws InterruptedException {
        String separator = "、";
        String originFilename = "D:\\Users\\zezhen.fan\\Documents\\Tencent Files\\842618916\\FileRecv\\MobileFile\\会说话的肘子-第一序列.txt";
        String targetFilename = originFilename.substring(0, originFilename.lastIndexOf(StrPool.DOT)) + StrPool.DOT + DateUtil.format(new DateTime(), DatePattern.CHINESE_DATE_TIME_FORMAT) + originFilename.substring(originFilename.lastIndexOf(StrPool.DOT));
        FileWriter fileWriter = new FileWriter(FileUtil.touch(targetFilename));
        FileReader fileReader = new FileReader(originFilename);
        List<String> readLines = fileReader.readLines();
        List<String> writeLines = new ArrayList<>(readLines);
        ThreadPoolExecutor threadPoolExecutor = PoolExecutors.defaultThreadPoolExecutor();
        CountDownLatch countDownLatch = new CountDownLatch(readLines.size());
        for (int i = 0, readLinesSize = readLines.size(); i < readLinesSize; i++) {
            int finalI = i;
            threadPoolExecutor.execute(() -> {
                try {
                    String readLine = readLines.get(finalI);
                    if (readLine.contains(separator)) {
                        String[] split = readLine.split(separator, 2);
                        String chapterNum = split[0].strip();
                        if (NumberUtil.isNumber(chapterNum)) {
                            writeLines.set(finalI, "第" + chapterNum + "章 " + split[1] + StrPool.CRLF);
                            return;
                        }
                    }
                    writeLines.set(finalI, readLine + StrPool.CRLF);
                } finally {
                    System.out.println(finalI);
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await(10, TimeUnit.MINUTES);
        writeLines.removeIf(CharSequenceUtil::isBlank);
        fileWriter.writeLines(writeLines);
        Assertions.assertTrue(true);
    }
}
