package com.github.fanzezhen.fun.framework.api.count;

import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.model.YApiModel;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 接口统计 接口
 *
 * @author fanzezhen
 * @since 3.4.3.3
 */
@Slf4j
@RestController
@RequestMapping("/web-count")
@ConditionalOnProperty(name = "fun.api.count.web.enabled", havingValue = "true", matchIfMissing = false)
public class FunApiCountController {
    @Resource
    private FunApiCountService webCountService;


    /**
     * 查询
     */
    @GetMapping("/map")
    public Map<String, LinkedHashMap<String, Integer>> mapResult() {
        return webCountService.map();
    }

    /**
     * 根据YApi接口数据导出excel，报告为YApi中每个接口的访问次数和返回值各字段的空值率
     */
    @PostMapping("/export/excel-by-y-api")
    public void exportExcel(@RequestBody List<YApiModel> yApiModelList, HttpServletResponse response) throws IOException {
        File file = webCountService.exportExcel(yApiModelList);
        response(response, file);
    }

    public static void response(HttpServletResponse response, File file) {
        // 清空输出流
        response.reset();
        // 设置强制下载不打开
        response.setContentType("application/force-download");
        // 设置文件名
        response.addHeader("Content-Disposition", "attachment;fileName=" +
            new String(file.getName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        byte[] buffer = new byte[1024];
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            // 获取response输出流
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (IOException e) {
            throw new ServiceException("HttpServletResponse 流写入失败", e);
        }
    }
}
