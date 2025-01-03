/*
 *
 *  *  Copyright (c) 2019-2020, 冷冷 (wangiegie@gmail.com).
 *  *  <p>
 *  *  Licensed under the GNU Lesser General Public License 3.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *  <p>
 *  * https://www.gnu.org/licenses/lgpl.html
 *  *  <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.github.fanzezhen.fun.framework.core.constant;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;

/**
 * 缓存的key 常量
 *
 * @author zezhen.fan
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
