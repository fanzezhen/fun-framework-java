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
 * 代理接口
 *
 * @author fanzezhen
 * @since 3.4.3.5
 */
@Slf4j
@RestController
@ConditionalOnProperty(value = "fun.proxy.enabled", havingValue = "true")
public class ProxyController {
    @Resource
    private ProxyProperties proxyProperties;

    /**
     * 代理静态资源
     */
    @GetMapping(value = {"/proxy/static/{filename}"})
    public ResponseEntity<byte[]> proxyStatic(@RequestParam String url,
                                              @PathVariable String filename,
                                              HttpServletRequest httpServletRequest) {
        if (CharSequenceUtil.isBlank(url)) {
            return ResponseEntity.noContent().build();
        }
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Map<String, String> paramMap = new HashMap<>(parameterMap.size(), 1f);
        parameterMap.forEach((k, v) -> paramMap.put(k, v[0]));
        paramMap.remove("url");
        for (ProxyProperties.Address address : proxyProperties.getAddressList()) {
            url = url.replace(address.getOrigin(), address.getTarget());
        }
        String queryString = HttpUtil.toParams(paramMap);
        if (url.contains("?")) {
            url = url + "&" + queryString;
        } else {
            url += queryString;
        }
        HttpRequest httpRequest = HttpUtil.createGet(url);
        return execute(httpRequest, filename);
    }

    /**
     * 代理json请求
     */
    @PostMapping(value = {"/proxy/json/{filename}"})
    public ResponseEntity<byte[]> proxyJson(@RequestParam String url,
                                            @PathVariable String filename,
                                            @RequestBody(required = false) JSONObject body) {
        if (CharSequenceUtil.isBlank(url)) {
            return ResponseEntity.noContent().build();
        }
        for (ProxyProperties.Address address : proxyProperties.getAddressList()) {
            url = url.replace(address.getOrigin(), address.getTarget());
        }
        HttpRequest httpRequest = HttpUtil.createPost(url);
        if (body != null) {
            httpRequest.body(body.toJSONString());
        }
        return execute(httpRequest, filename);
    }

    /**
     * 代理文件请求
     */
    @PostMapping(value = {"/proxy/multipart-file/{filename}"})
    public ResponseEntity<byte[]> proxyMultipartFile(@RequestParam String url,
                                                     @PathVariable String filename,
                                                     HttpServletRequest httpServletRequest) {
        if (CharSequenceUtil.isBlank(url)) {
            return ResponseEntity.noContent().build();
        }
        for (ProxyProperties.Address address : proxyProperties.getAddressList()) {
            url = url.replace(address.getOrigin(), address.getTarget());
        }
        HttpRequest httpRequest = HttpUtil.createPost(url);
        if (httpServletRequest instanceof StandardMultipartHttpServletRequest standardMultipartHttpServletRequest) {
            Map<String, MultipartFile> fileMap = standardMultipartHttpServletRequest.getFileMap();
            fileMap.forEach((k, v) -> {
                try {
                    httpRequest.fileForm().put(k, new InputStreamResource(v.getInputStream()));
                } catch (Exception e) {
                    log.warn("", e);
                }
            });
        }
        return execute(httpRequest, filename);
    }

    /**
     * 代理二进制请求
     */
    @PostMapping(value = {"/proxy/binary/{filename}"})
    public ResponseEntity<byte[]> proxyBinary(@RequestParam String url,
                                              @PathVariable String filename,
                                              @RequestBody(required = false) byte[] body) {
        if (CharSequenceUtil.isBlank(url)) {
            return ResponseEntity.noContent().build();
        }
        for (ProxyProperties.Address address : proxyProperties.getAddressList()) {
            url = url.replace(address.getOrigin(), address.getTarget());
        }
        HttpRequest httpRequest = HttpUtil.createPost(url);
        httpRequest.body(body);
        return execute(httpRequest, filename);
    }

    @SneakyThrows
    private static ResponseEntity<byte[]> execute(HttpRequest httpRequest, String filename) {
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        try (HttpResponse httpResponse = httpRequest.execute()) {
            return ResponseEntity.status(httpResponse.getStatus())
                    .header("Content-Disposition", ContentDisposition.builder("attachment").filename(encodedFilename).build().toString())
                    .header("Set-Cookie", "cookiename=cookievalue; path=/; Domain=domainvaule; Max-age=seconds; HttpOnly")
                    .body(httpResponse.bodyBytes());
        }
    }

}
