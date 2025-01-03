package com.github.fanzezhen.fun.framework.log.tool;

import com.github.fanzezhen.fun.framework.core.util.HttpUtil;
import com.github.fanzezhen.fun.framework.core.util.SysClientInfoUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

/**
 * @author zezhen.fan
 */
@Slf4j
public class LogTask {
    public static TimerTask log(String logApi, Map<String, String> logMap) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    log.info("日志接口地址：" + logApi);
                    log.info("日志记录参数：" + logMap);
                    log.info("日志请求结果：" + HttpUtil.doPost(logApi, logMap));
                } catch (Exception e) {
                    log.error("创建登录日志异常!", e);
                }
            }
        };
    }

    public static TimerTask loginLog(String logApi, String username, String logType, String os, String ip,
                                     String browserName, String browserVersion, String remark, String userId) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    log.info(String.valueOf(log(logApi, new HashMap<>(8, 1) {{
                        put("username", username);
                        put("logType", logType);
                        put("os", os);
                        put("ip", ip);
                        put("browserName", browserName);
                        put("browserVersion", browserVersion);
                        put("remark", remark);
                        put("createUserId", userId);
                    }})));
                } catch (Exception e) {
                    log.error("创建登录日志异常!", e);
                }
            }
        };
    }

    public static TimerTask loginLog(HttpServletRequest request, String logApi, String username, String logType,
                                     String remark, String userId) {
        return loginLog(logApi, username, logType, SysClientInfoUtil.getOsName(request), SysClientInfoUtil.getIp(request),
                SysClientInfoUtil.getBrowserName(request), SysClientInfoUtil.getBrowserVersion(request), remark, userId);
    }

    public static TimerTask loginLog(HttpServletRequest request, Map<String, String> logMap) {
        return loginLog(request, logMap.get("logApi"), logMap.get("username"), logMap.get("logType"),
                logMap.get("remark"), logMap.get("userId"));
    }

    public static TimerTask loginLog(String logApi, HttpServletRequest request, Map<String, String> logMap) {
        logMap.putAll(SysClientInfoUtil.getAll(request));
        return log(logApi, logMap);
    }
}
