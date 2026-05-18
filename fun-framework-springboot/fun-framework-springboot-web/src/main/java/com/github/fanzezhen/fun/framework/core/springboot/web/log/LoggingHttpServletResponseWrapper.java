package com.github.fanzezhen.fun.framework.core.springboot.web.log;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * HTTP响应包装器，支持多次读取响应体.
 * <p>
 * 将响应体缓存到内存中，允许读取响应内容用于日志记录等场景。
 * 同时支持记录响应中设置的Cookie信息。
 */
public class LoggingHttpServletResponseWrapper extends HttpServletResponseWrapper {
    /**
     * 响应中设置的Cookie列表.
     */
    @Getter
    private final List<Cookie> cookies = new ArrayList<>();

    /**
     * 响应体缓冲区.
     */
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    /**
     * 输出流.
     */
    private ServletOutputStream outputStream;

    /**
     * 字符输出流.
     */
    private PrintWriter writer;

    /**
     * 构造函数.
     *
     * @param response 原始HTTP响应
     */
    public LoggingHttpServletResponseWrapper(final HttpServletResponse response) {
        super(response);
    }

    /**
     * 添加Cookie并记录到列表.
     *
     * @param cookie Cookie对象
     */
    @Override
    public void addCookie(final Cookie cookie) {
        super.addCookie(cookie);
        cookies.add(cookie);
    }

    /**
     * 获取响应输出流.
     *
     * @return 响应输出流
     * @throws IOException IO异常
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException("getWriter() has already been called on this response.");
        }

        if (outputStream == null) {
            outputStream = new ServletOutputStream() {
                @Override
                public void write(final int b) throws IOException {
                    buffer.write(b);
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(final WriteListener listener) {
                    // Do nothing
                }
            };
        }

        return outputStream;
    }

    /**
     * 获取字符输出流.
     *
     * @return 字符输出流
     * @throws IOException IO异常
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputStream != null) {
            throw new IllegalStateException("getOutputStream() has already been called on this response.");
        }

        if (writer == null) {
            writer = new PrintWriter(buffer, true, StandardCharsets.UTF_8);
        }

        return writer;
    }

    /**
     * 刷新输出缓冲区.
     *
     * @throws IOException IO异常
     */
    @Override
    public void flushBuffer() throws IOException {
        if (writer != null) {
            writer.flush();
        } else if (outputStream != null) {
            outputStream.flush();
        }
    }

    /**
     * 获取缓存的响应体字节数组.
     *
     * @return 响应体字节数组
     * @throws IOException IO异常
     */
    public byte[] toByteArray() throws IOException {
        flushBuffer();
        return buffer.toByteArray();
    }
}
