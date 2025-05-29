package com.github.fanzezhen.fun.framework.core.cache;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;

/**
 * 缓存的key 常量
 *
 * @author fanzezhen
 */
public interface CacheConstant {

    /**
     * oauth 缓存前缀
     */
    String PROJECT_OAUTH_ACCESS = "pig_oauth:access:";

    /**
     * 验证码前缀
     */
    String DEFAULT_CODE_KEY = "DEFAULT_CODE_KEY:";


    /**
     * 菜单信息缓存
     */
    String MENU_DETAILS = "menu_details";

    /**
     * 权限信息缓存
     */
    String PERMISSION_DETAILS = "permission_details";

    /**
     * 用户信息缓存
     */
    String USER_DETAILS = "user_details";

    /**
     * 字典信息缓存
     */
    String DICT_DETAILS = "dict_details";


    /**
     * oauth 客户端信息
     */
    String CLIENT_DETAILS_KEY = "pig_oauth:client:details";


    /**
     * 参数缓存
     */
    String PARAMS_DETAILS = "params_details";

    static TimedCache<String, String> getHourTimedCacheInstance() {
        return HourCacheSingletonHolder.HOUR_TIMED_CACHE;
    }

    class HourCacheSingletonHolder {
        private HourCacheSingletonHolder() {
        }

        private static final TimedCache<String, String> HOUR_TIMED_CACHE = CacheUtil.newTimedCache(60 * 60 * 1000L);

        static {
            HOUR_TIMED_CACHE.schedulePrune(60 * 60 * 1000L);
        }
    }
}
