package com.github.fanzezhen.common.log.support.web;

import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;


public class LoggingHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public LoggingHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = getBodyString(request).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

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
            public void setReadListener(ReadListener readListener) {
                // 不需要实现
            }
        };
    }

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
    
    public static String getBodyString(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (InputStream inputStream = request.getInputStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

}
