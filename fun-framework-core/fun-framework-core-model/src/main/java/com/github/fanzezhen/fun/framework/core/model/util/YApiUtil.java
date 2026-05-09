package com.github.fanzezhen.fun.framework.core.model.util;

import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.model.common.YApiModel;

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
     * YApi接口数据导出为Excel文件
     * <p>
     * 注意：返回的临时文件位于系统临时目录（如 /tmp 或 C:\Users\xxx\AppData\Local\Temp），
     * 调用方负责使用后删除文件，避免磁盘空间泄漏。
     * 建议在 finally 块或 try-with-resources 中调用 {@code file.delete()}。
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
