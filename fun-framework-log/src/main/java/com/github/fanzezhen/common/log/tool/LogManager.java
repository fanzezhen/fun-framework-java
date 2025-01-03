/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.fanzezhen.fun.framework.log.tool;

import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 日志管理器
 *
 * @author zezhen.fan
 * @date 2017-03-30 16:29
 */
public class LogManager {

    /**
     * 线程池size
     */
    private static final int CORE_POOL_SIZE = 10;
    /**
     * 日志记录操作延时
     */
    private static final int OPERATE_DELAY_TIME = 10;

    /**
     * 异步操作记录日志的线程池
     */
    private final ScheduledThreadPoolExecutor executor =
            new ScheduledThreadPoolExecutor(CORE_POOL_SIZE, new LogTreadFactory());

    private LogManager() {
    }

    public static final LogManager logManager = new LogManager();

    public static LogManager me() {
        return logManager;
    }

    public void executeLog(TimerTask task) {
        executor.schedule(task, OPERATE_DELAY_TIME, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {
        LogManager.me().executeLog(LogTask.loginLog("http://localhost:10002/log/record/login", "admin",
                "1", "windows", "", "G", "10.0", "", "adminId"));
    }
}
