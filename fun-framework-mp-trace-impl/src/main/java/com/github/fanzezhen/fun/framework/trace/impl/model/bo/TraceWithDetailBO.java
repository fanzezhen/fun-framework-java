package com.github.fanzezhen.fun.framework.trace.impl.model.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 痕迹表
 *
 * @author fanzezhen
 * @since 3.4.3.1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class TraceWithDetailBO extends TraceBO {

    /**
     * 详情列表
     */
    private List<TraceDetailBO> detailList;

    public String getTypeName() {
        switch (type) {
            case INSERT:
                return "新增";
            case UPDATE:
                return "修改";
            case DELETE:
                return "删除";
            default:
                return "其他";
        }
    }

    public String getDetails() {
        if (detailList == null) {
            return null;
        }
        return detailList.stream().map(detail -> detail.getName() + "由“" + detail.getOldValue() + "”改为“" + detail.getNewValue() + "”").collect(Collectors.joining(";\n"));
    }

}
