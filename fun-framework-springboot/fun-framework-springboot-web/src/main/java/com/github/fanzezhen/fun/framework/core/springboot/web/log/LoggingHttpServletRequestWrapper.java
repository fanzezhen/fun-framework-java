package com.github.fanzezhen.fun.framework.core.springboot.web.log;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;


/**
 * HTTP请求包装器，支持多次读取请求体.
 * <p>
 * 将请求体缓存到内存中，允许多次读取。
 * 用于日志记录等需要读取请求体但不能消耗原始流的场景。
 */
public class LoggingHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 缓存的请求体字节数组.
     */
    private final byte[] body;

    /**
     * 构造函数，读取并缓存请求体.
     *
     * @param request 原始HTTP请求
     * @throws IOException 读取请求体失败时抛出
     */
    public LoggingHttpServletRequestWrapper(final HttpServletRequest request) throws IOException {
        super(request);
        body = getBodyString(request).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 获取请求体读取器.
     *
     * @return 请求体读取器
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    /**
     * 获取请求体输入流.
     *
     * @return 请求体输入流
     */
    @Override
    public ServletInputStream getInputStream() {

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);

        return new ServletInputStream() {

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(final ReadListener readListener) {
                // 不需要实现
            }
        };
    }

    /**
     * 获取所有请求头.
     *
     * @return 请求头JSON对象
     */
    public JSONObject getHeader() {
        Enumeration<String> headerNames = getHeaderNames();
        JSONObject header = new JSONObject();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = getHeader(headerName);
            header.put(headerName, headerValue);
        }
        return header;
    }

    /**
     * 从请求中读取请求体字符串.
     *
     * @param request HTTP请求对象
     * @return 请求体字符串
     * @throws IOException 读取失败时抛出
     */
    public static String getBodyString(final HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (InputStream inputStream = request.getInputStream();
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

}
