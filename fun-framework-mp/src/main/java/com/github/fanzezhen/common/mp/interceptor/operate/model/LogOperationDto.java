package com.github.fanzezhen.fun.framework.mp.interceptor.operate.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.github.fanzezhen.fun.framework.mp.enums.log.OperationLogTypeEnum;
import com.github.fanzezhen.fun.framework.core.model.bo.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author fanzezhen
 * @since 2021-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name="LogOperation对象", description="操作日志表")
@Accessors(chain = true)
public class LogOperationDto extends BaseDTO<String> {

    @Schema(name = "业务Id")
    private String bizId;

    @Schema(name = "表名称")
    private String tableName;

    @EnumValue
    @Schema(name = "操作类型")
    @JSONField(serialzeFeatures= SerializerFeature.WriteEnumUsingToString)
    private OperationLogTypeEnum operationType;

    @Schema(name = "操作模块")
    private String module;

    @Schema(name = "IP地址")
    private String ipAddress;

    @Schema(name = "设备号")
    private String deviceNum;

    @Schema(name = "备注")
    private String remark;

    @Schema(name = "操作人名称")
    private String operationUsername;

    @Schema(name = "APP标识")
    private String appCode;

}
