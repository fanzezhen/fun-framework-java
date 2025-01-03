package com.github.fanzezhen.fun.framework.core;

import com.github.fanzezhen.fun.framework.core.thread.ExecutorHolder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;


/**
 * @author zezhen.fan
 */
@Slf4j
@Disabled
class ExecutorHolderTest {
    @Test
    @Disabled("演示用")
    void test() throws InterruptedException {
        ExecutorHolder<Object> executorHolder = ExecutorHolder.create().addTask(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("1");
        }).addTask(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("2");
        }).addTask(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("3");
        }).addTask(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("4");
        }).addTask(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("5");
        });
        Thread.sleep(1000);
        System.out.println("6");
        executorHolder.get();
        executorHolder.addTask(() -> {
            System.out.println("7");
            return 7;
        });
        System.out.println(executorHolder.get());
        Assertions.assertNotNull(executorHolder.get());
    }
}
