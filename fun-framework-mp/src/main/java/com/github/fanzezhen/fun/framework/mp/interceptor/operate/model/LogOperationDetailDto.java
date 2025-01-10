package com.github.fanzezhen.fun.framework.mp.interceptor.operate.model;

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
 * @since 2020-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name="LogOperationDetail对象", description="操作日志详情表")
@Accessors(chain = true)
public class LogOperationDetailDto extends BaseDTO {

    @Schema(name = "操作日志Id")
    private String logId;

    @Schema(name = "列字段")
    private String tableColumn;

    @Schema(name = "列名称")
    private String columnName;

    @Schema(name = "oldValue")
    private String oldValue;

    @Schema(name = "newValue")
    private String newValue;

    @Schema(name = "备注")
    private String remark;

    @Schema(name = "APP标识")
    private String appCode;

}
