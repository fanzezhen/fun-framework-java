package com.github.fanzezhen.fun.framework.security.sa.token.sso.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * SSO 用户信息模型。
 * <p>
 * 封装 SSO 单点登录系统中的用户详细信息。
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonSerialize
public class SsoInfo implements Serializable {

    /**
     * 职位显示名称。
     */
    private String positionShow;

    /**
     * 导师姓名。
     */
    private String mentorName;

    /**
     * 公司名称。
     */
    private String companyName;

    /**
     * 组织OID。
     */
    private Integer organizationOid;

    /**
     * 部门名称。
     */
    private String divisionName;

    /**
     * 虚线经理姓名。
     */
    private String dottedLineManagerName;

    /**
     * 用户ID。
     */
    private Integer uid;

    /**
     * 直线经理姓名。
     */
    private String straightLineManagerName;

    /**
     * 部门OID。
     */
    private Integer departmentOid;

    /**
     * 工号。
     */
    private String workCode;

    /**
     * 英文名。
     */
    private String enName;

    /**
     * 虚线经理邮箱。
     */
    private String dottedLineManagerEmail;

    /**
     * 事业部OID。
     */
    private Integer divisionOid;

    /**
     * 部门负责人UID。
     */
    private Integer departmentChargeUid;

    /**
     * 虚线经理UID。
     */
    private Integer dottedLineManagerUid;

    /**
     * 领导UID。
     */
    private Integer leaderUid;

    /**
     * 邮箱。
     */
    private String email;

    /**
     * 部门树路径字符串。
     */
    private String departmentTreePathStr;

    /**
     * 直线经理UID。
     */
    private Integer straightLineManagerUid;

    /**
     * 部门名称。
     */
    private String departmentName;

    /**
     * 导师UID。
     */
    private Integer mentorUid;

    /**
     * 领导邮箱。
     */
    private String leaderEmail;

    /**
     * 手机号。
     */
    private String mobile;

    /**
     * 头像URL。
     */
    private String avatar;

    /**
     * 部门树路径。
     */
    private String departmentTreePath;

    /**
     * 直线经理邮箱。
     */
    private String straightLineManagerEmail;

    /**
     * 角色ID列表。
     */
    private List<Integer> rids;

    /**
     * 员工信息。
     */
    private Integer empInfo;

    /**
     * 领导姓名。
     */
    private String leaderName;

    /**
     * 姓名。
     */
    private String name;

    /**
     * 是否负责人（1: 是，0: 否）。
     */
    private Integer isCharge;

    /**
     * 状态。
     */
    private Integer status;
}
