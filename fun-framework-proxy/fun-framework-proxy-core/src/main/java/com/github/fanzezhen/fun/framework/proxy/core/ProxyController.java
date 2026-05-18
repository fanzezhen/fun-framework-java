package com.github.fanzezhen.fun.framework.proxy.core;

import cn.hutool.core.io.resource.InputStreamResource;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import jakarta.annotation.Resource;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 代理接口，提供HTTP请求代理转发功能。
 * <p>
 * 主要功能：
 * <ul>
 *   <li>静态资源代理（GET请求）</li>
 *   <li>JSON数据代理（POST请求）</li>
 *   <li>文件上传代理（POST multipart/form-data）</li>
 *   <li>二进制数据代理（POST binary）</li>
 * </ul>
 * <p>
 * 所有代理接口会根据配置的地址映射（origin → target）
 * 自动转换目标地址。
 *
 * @since 3.4.3.5
 */
@Slf4j
@RestController
@ConditionalOnProperty(value = "fun.proxy.enabled", havingValue = "true")
public class ProxyController {
    /**
     * HashMap 的负载因子，用于优化性能。
     */
    private static final float LOAD_FACTOR = 1f;

    /**
     * 代理配置属性。
     */
    @Resource
    private ProxyProperties proxyProperties;

    /**
     * 代理静态资源（GET请求）。
     *
     * @param url                待代理的URL地址
     * @param filename           响应文件名
     *                           （用于Content-Disposition头）
     * @param httpServletRequest 原始HTTP请求
     *                           （用于提取查询参数）
     * @return 代理响应结果（字节数组）
     */
    @GetMapping(value = {"/proxy/static/{filename}"})
    public ResponseEntity<byte[]> proxyStatic(
            @RequestParam final String url,
            @PathVariable final String filename,
            final HttpServletRequest httpServletRequest) {
        if (CharSequenceUtil.isBlank(url)) {
            return ResponseEntity.noContent().build();
        }
        Map<String, String[]> parameterMap =
                httpServletRequest.getParameterMap();
        Map<String, String> paramMap =
                new HashMap<>(parameterMap.size(), LOAD_FACTOR);
        parameterMap.forEach((k, v) -> paramMap.put(k, v[0]));
        paramMap.remove("url");
        String targetUrl = url;
        for (ProxyProperties.Address address :
                proxyProperties.getAddressList()) {
            targetUrl = targetUrl.replace(
                    address.getOrigin(), address.getTarget());
        }
        String queryString = HttpUtil.toParams(paramMap);
        if (targetUrl.contains("?")) {
            targetUrl = targetUrl + "&" + queryString;
        } else {
            targetUrl += queryString;
        }
        HttpRequest httpRequest = HttpUtil.createGet(targetUrl);
        return execute(httpRequest, filename);
    }

    /**
     * 代理JSON请求（POST请求）。
     *
     * @param url      待代理的URL地址
     * @param filename 响应文件名
     *                 （用于Content-Disposition头）
     * @param body     请求体JSON数据
     * @return 代理响应结果（字节数组）
     */
    @PostMapping(value = {"/proxy/json/{filename}"})
    public ResponseEntity<byte[]> proxyJson(
            @RequestParam final String url,
            @PathVariable final String filename,
            @RequestBody(required = false) final JSONObject body) {
        if (CharSequenceUtil.isBlank(url)) {
            return ResponseEntity.noContent().build();
        }
        String targetUrl = url;
        for (ProxyProperties.Address address :
                proxyProperties.getAddressList()) {
            targetUrl = targetUrl.replace(
                    address.getOrigin(), address.getTarget());
        }
        HttpRequest httpRequest = HttpUtil.createPost(targetUrl);
        if (body != null) {
            httpRequest.body(body.toJSONString());
        }
        return execute(httpRequest, filename);
    }

    /**
     * 代理文件上传请求（POST multipart/form-data）。
     *
     * @param url                待代理的URL地址
     * @param filename           响应文件名
     *                           （用于Content-Disposition头）
     * @param httpServletRequest 原始HTTP请求
     *                           （用于提取上传的文件）
     * @return 代理响应结果（字节数组）
     */
    @PostMapping(value = {"/proxy/multipart-file/{filename}"})
    public ResponseEntity<byte[]> proxyMultipartFile(
            @RequestParam final String url,
            @PathVariable final String filename,
            final HttpServletRequest httpServletRequest) {
        if (CharSequenceUtil.isBlank(url)) {
            return ResponseEntity.noContent().build();
        }
        String targetUrl = url;
        for (ProxyProperties.Address address :
                proxyProperties.getAddressList()) {
            targetUrl = targetUrl.replace(
                    address.getOrigin(), address.getTarget());
        }
        HttpRequest httpRequest = HttpUtil.createPost(targetUrl);
        if (httpServletRequest instanceof
                StandardMultipartHttpServletRequest
                        standardMultipartHttpServletRequest) {
            Map<String, MultipartFile> fileMap =
                    standardMultipartHttpServletRequest.getFileMap();
            fileMap.forEach((k, v) -> {
                try {
                    httpRequest.fileForm().put(k,
                            new InputStreamResource(v.getInputStream()));
                } catch (Exception e) {
                    log.warn("", e);
                }
            });
        }
        return execute(httpRequest, filename);
    }

    /**
     * 代理二进制数据请求（POST binary）。
     *
     * @param url      待代理的URL地址
     * @param filename 响应文件名
     *                 （用于Content-Disposition头）
     * @param body     请求体二进制数据
     * @return 代理响应结果（字节数组）
     */
    @PostMapping(value = {"/proxy/binary/{filename}"})
    public ResponseEntity<byte[]> proxyBinary(
            @RequestParam final String url,
            @PathVariable final String filename,
            @RequestBody(required = false) final byte[] body) {
        if (CharSequenceUtil.isBlank(url)) {
            return ResponseEntity.noContent().build();
        }
        String targetUrl = url;
        for (ProxyProperties.Address address :
                proxyProperties.getAddressList()) {
            targetUrl = targetUrl.replace(
                    address.getOrigin(), address.getTarget());
        }
        HttpRequest httpRequest = HttpUtil.createPost(targetUrl);
        httpRequest.body(body);
        return execute(httpRequest, filename);
    }

    /**
     * 执行HTTP请求并返回响应。
     *
     * @param httpRequest HTTP请求对象
     * @param filename    响应文件名
     * @return 响应实体
     */
    @SneakyThrows
    private static ResponseEntity<byte[]> execute(
            final HttpRequest httpRequest, final String filename) {
        String encodedFilename =
                URLEncoder.encode(filename, StandardCharsets.UTF_8);
        try (HttpResponse httpResponse = httpRequest.execute()) {
            return ResponseEntity.status(httpResponse.getStatus())
                    .header("Content-Disposition",
                            ContentDisposition.builder("attachment")
                                    .filename(encodedFilename)
                                    .build().toString())
                    .header("Set-Cookie",
                            "cookiename=cookievalue; path=/; "
                                    + "Domain=domainvaule; "
                                    + "Max-age=seconds; HttpOnly")
                    .body(httpResponse.bodyBytes());
        }
    }

}
