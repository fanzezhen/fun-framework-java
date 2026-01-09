package com.github.fanzezhen.fun.framework.core.data.util;

import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.model.YApiModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * YApi工具类
 */
public class YApiUtil {
    private YApiUtil() {
    }

    /**
     * json转Excel
     */
    public static File jsonToExcel(List<YApiModel> modelList) throws IOException {
        List<JSONObject> rowList = new ArrayList<>();
        for (YApiModel yApiModel : modelList) {
            for (YApiModel.Api api : yApiModel.getList()) {
                rowList.add(new JSONObject().fluentPut("模块", yApiModel.getName()).fluentPut("接口名", api.getTitle()).fluentPut("接口地址", api.getPath()));
            }
        }
        File tempFile = File.createTempFile("YApi", ".xlsx");
        ExcelUtil.getWriter(true).write(rowList).flush(tempFile);
        return tempFile;
    }
}
