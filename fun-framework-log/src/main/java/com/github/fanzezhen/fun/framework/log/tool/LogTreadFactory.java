package com.github.fanzezhen.fun.framework.log.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zezhen.fan
 */
@Slf4j
public class LogTreadFactory implements ThreadFactory {

    private final AtomicInteger mThreadNum = new AtomicInteger(1);

    @Override
    public Thread newThread(@NonNull Runnable r) {
        Thread t = new Thread(r, "my-thread-" + mThreadNum.getAndIncrement());
        log.debug(t.getName() + " has been created");
        return t;
    }
}
