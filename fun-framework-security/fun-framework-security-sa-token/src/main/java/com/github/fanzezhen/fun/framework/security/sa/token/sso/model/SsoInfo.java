package com.github.fanzezhen.fun.framework.security.sa.token.sso.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonSerialize
public class SsoInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String positionShow;
    private String mentorName;
    private String companyName;
    private Integer organizationOid;
    private String divisionName;
    private String dottedLineManagerName;
    private Integer uid;
    private String straightLineManagerName;
    private Integer departmentOid;
    private String workCode;
    private String enName;
    private String dottedLineManagerEmail;
    private Integer divisionOid;
    private Integer departmentChargeUid;
    private Integer dottedLineManagerUid;
    private Integer leaderUid;
    private String email;
    private String departmentTreePathStr;
    private Integer straightLineManagerUid;
    private String departmentName;
    private Integer mentorUid;
    private String leaderEmail;
    private String mobile;
    private String avatar;
    private String departmentTreePath;
    private String straightLineManagerEmail;
    private List<Integer> rids;
    private Integer empInfo;
    private String leaderName;
    private String name;
    private Integer isCharge;
    private Integer status;
}
