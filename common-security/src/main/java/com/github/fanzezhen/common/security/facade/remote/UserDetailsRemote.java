package com.github.fanzezhen.common.security.facade.remote;

import com.github.fanzezhen.common.core.model.dto.SysPermissionDto;
import com.github.fanzezhen.common.core.model.dto.SysUserDto;
import com.github.fanzezhen.common.core.model.response.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author zezhen.fan
 */
@FeignClient(url = "${security.remote.user.detail.url}", name = "userDetailsRemote")
public interface UserDetailsRemote {

    /**
     * 加载用户信息
     *
     * @param username 用户名
     * @param appCode  APP标识
     * @return 用户信息
     */
    @GetMapping("/user/by/username")
    Result<SysUserDto> loadUserByUsername(@RequestParam("username") String username, @RequestParam("appCode") String appCode);

    /**
     * 权限列表
     *
     * @param appCode APP标识
     * @return 权限列表
     */
    @GetMapping("/permission/list")
    Result<List<SysPermissionDto>> listPermission(@RequestParam("appCode") String appCode);
}
