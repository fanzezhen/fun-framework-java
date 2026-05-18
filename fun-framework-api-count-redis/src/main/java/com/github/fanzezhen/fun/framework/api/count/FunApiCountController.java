package com.github.fanzezhen.fun.framework.api.count;

import com.github.fanzezhen.fun.framework.core.model.constant.NormalTypeConstant;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.model.common.YApiModel;
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
 * 接口统计控制器.
 *
 * @since 3.4.3.3
 */
@Slf4j
@RestController
@RequestMapping("/web-count")
@ConditionalOnProperty(name = "fun.api.count.web.enabled", havingValue = "true", matchIfMissing = false)
public class FunApiCountController {
    /**
     * API统计服务.
     */
    @Resource
    private FunApiCountService webCountService;


    /**
     * 查询所有API统计数据.
     * <p>
     * 返回格式：{URL → {字段名 → 访问次数}}
     *
     * @return API统计数据Map
     */
    @GetMapping("/map")
    public Map<String, LinkedHashMap<String, Integer>> mapResult() {
        return webCountService.map();
    }

    /**
     * 根据YApi接口数据导出Excel统计报告.
     * <p>
     * 报告内容包括：
     * <ul>
     *   <li>每个接口的访问次数</li>
     *   <li>返回值各字段的空值率</li>
     *   <li>无效接口标识</li>
     * </ul>
     *
     * @param yApiModelList YApi接口数据列表
     * @param response      HTTP响应对象，用于下载文件
     * @throws IOException 文件处理异常
     */
    @PostMapping("/export/excel-by-y-api")
    public void exportExcel(@RequestBody final List<YApiModel> yApiModelList, final HttpServletResponse response) throws IOException {
        File file = webCountService.exportExcel(yApiModelList);
        response(response, file);
    }

    /**
     * 将文件写入HTTP响应流，触发浏览器下载.
     *
     * @param response HTTP响应对象
     * @param file     要下载的文件
     * @throws ServiceException 流写入失败时抛出
     */
    public static void response(final HttpServletResponse response, final File file) {
        // 清空输出流
        response.reset();
        // 设置强制下载不打开
        response.setContentType("application/force-download");
        // 设置文件名
        response.addHeader("Content-Disposition", "attachment;fileName=" +
            new String(file.getName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        byte[] buffer = new byte[NormalTypeConstant.INT_1024];
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
