package com.github.fanzezhen.fun.framework.mp.model.dto;

import com.github.fanzezhen.fun.framework.core.annotion.OperateLog;
import com.github.fanzezhen.fun.framework.mp.model.SysUserDict;
import com.github.fanzezhen.fun.framework.mp.base.entity.tenant.BaseTenantEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author zezhen.fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@OperateLog(dictClass = SysUserDict.class, tableName = "sys_user")
public class SysUserDto extends BaseTenantEntity {
    /**
     * 单位名称
     */
    @Schema(name = "单位名称")
    private String unitName;

    /**
     * 权限标识集合
     */
    @Schema(name = "权限标识集合")
    private List<SysPermissionDto> sysPermissionDtoList;

    /**
     * 角色ID集合
     */
    @Schema(name = "角色ID集合")
    private Set<String> roleIdSets;
    /**
     * 角色名称集合
     */
    @Schema(name = "角色名称集合")
    private Set<String> roleNameSets;
    /**
     * 角色类型集合
     */
    @Schema(name = "角色类型集合")
    private Set<Integer> roleTypeSets;

    // 以下为基本信息
    /**
     * 用户名
     */
    @Schema(name = "用户名")
    private String username;

    /**
     * 密码
     */
    @Schema(name = "密码")
    private String password;

    /**
     * 昵称
     */
    @Schema(name = "昵称")
    private String nickname;

    /**
     * 头像地址
     */
    @Schema(name = "头像地址")
    private String avatar;

    /**
     * 邮箱
     */
    @Schema(name = "邮箱")
    private String email;

    /**
     * 联系电话
     */
    @Schema(name = "联系电话")
    private String phone;

    /**
     * 性别（0--女；1--男；2--未知的性别；3--未说明的性别）
     */
    @Schema(name = "性别（0--女；1--男；2--未知的性别；3--未说明的性别）")
    private Integer sex;

    /**
     * 所属部门
     */
    @Schema(name = "所属部门")
    private String department;

    /**
     * 最后操作时间
     */
    @Schema(name = "最后操作时间")
    private LocalDateTime lastTime;

    /**
     * 所属应用代码
     */
    @Schema(name = "所属应用代码")
    private String appCode;
}
