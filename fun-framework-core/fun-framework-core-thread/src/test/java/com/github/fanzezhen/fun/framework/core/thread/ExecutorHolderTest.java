package com.github.fanzezhen.fun.framework.core.thread;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

/**
 * @author fanzezhen
 */
@Slf4j
class ExecutorHolderTest {

    @Test
    void testAsyncExec() {
        Assertions.assertDoesNotThrow(() -> ExecutorHolder.asyncExec(() -> {
            ThreadUtil.sleep(1);
            log.info("testAsyncExec asyncExec running ");
        }));
        log.info(" testAsyncExec end ");
    }

    @Test
    void test() {
        long timeMillis = System.currentTimeMillis();
        Assertions.assertTimeout(Duration.ofMillis(2999), () -> {
            ExecutorHolder<String> executorHolder = ExecutorHolder.<String>create()
                .addTask(() -> {
                    ThreadUtil.sleep(1200);
                    return "a";
                })
                .addTask(() -> {
                    ThreadUtil.sleep(1000);
                    return "b";
                })
                .addTask(() -> {
                    ThreadUtil.sleep(800);
                    return "c";
                });
            List<String> list = executorHolder.get();
            Assertions.assertEquals("a", list.get(0));
            Assertions.assertEquals("b", list.get(1));
            Assertions.assertEquals("c", list.get(2));
        });
        log.info("三个任务用时小于3秒完成 {}", System.currentTimeMillis() - timeMillis);
    }
}
