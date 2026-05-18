package com.github.fanzezhen.fun.framework.core.cache;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.github.fanzezhen.fun.framework.core.model.constant.NormalTypeConstant;

/**
 * 缓存的key常量.
 * <p>
 * 定义了系统中常用的缓存键前缀和名称，以及时间相关的常量。
 */
public interface CacheConstant {

    /**
     * oauth缓存前缀.
     */
    String PROJECT_OAUTH_ACCESS = "pig_oauth:access:";

    /**
     * 验证码前缀.
     */
    String DEFAULT_CODE_KEY = "DEFAULT_CODE_KEY:";

    /**
     * 菜单信息缓存.
     */
    String MENU_DETAILS = "menu_details";

    /**
     * 权限信息缓存.
     */
    String PERMISSION_DETAILS = "permission_details";

    /**
     * 用户信息缓存.
     */
    String USER_DETAILS = "user_details";

    /**
     * 字典信息缓存.
     */
    String DICT_DETAILS = "dict_details";

    /**
     * oauth客户端信息.
     */
    String CLIENT_DETAILS_KEY = "pig_oauth:client:details";

    /**
     * 参数缓存.
     */
    String PARAMS_DETAILS = "params_details";

    /**
     * 获取小时级定时缓存实例.
     * <p>
     * 返回单例的小时级定时缓存，用于存储临时数据。
     *
     * @return 小时级定时缓存实例
     */
    static TimedCache<String, Object> getHourTimedCacheInstance() {
        return HourCacheSingletonHolder.HOUR_TIMED_CACHE;
    }

    /**
     * 小时级缓存单例持有者.
     * <p>
     * 使用静态内部类实现延迟加载的线程安全单例。
     */
    final class HourCacheSingletonHolder {
        /**
         * 私有构造函数，防止实例化.
         */
        private HourCacheSingletonHolder() {
        }

        /**
         * 小时级定时缓存实例.
         */
        private static final TimedCache<String, Object> HOUR_TIMED_CACHE = CacheUtil.newTimedCache(NormalTypeConstant.LONG_ONE_HOUR_MILLIS);

        static {
            HOUR_TIMED_CACHE.schedulePrune(NormalTypeConstant.LONG_ONE_HOUR_MILLIS);
        }
    }
}
