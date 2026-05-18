package com.github.fanzezhen.fun.framework.core.model.util;

import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.model.common.YApiModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * YApi 工具类
 * <p>
 * 提供 YApi 接口文档数据的导出功能，支持将接口数据导出为 Excel 文件。
 * </p>
 */
public class YApiUtil {
    /**
     * 工具类不允许实例化
     */
    private YApiUtil() {
    }

    /**
     * 将 YApi 接口数据导出为 Excel 文件
     * <p>
     * <b>注意：</b>返回的临时文件位于系统临时目录（如 /tmp 或 C:\Users\xxx\AppData\Local\Temp），
     * 调用方负责使用后删除文件，避免磁盘空间泄漏。
     * 建议在 finally 块或 try-with-resources 中调用 {@code file.delete()}。
     * </p>
     *
     * @param modelList YApi 模型列表
     * @return Excel 临时文件
     * @throws IOException 如果创建或写入文件失败
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
