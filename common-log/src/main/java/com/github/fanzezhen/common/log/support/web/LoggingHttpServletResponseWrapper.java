package com.github.fanzezhen.common.log.support.web;

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

public class LoggingHttpServletResponseWrapper extends HttpServletResponseWrapper {
    @Getter
    private final List<Cookie> cookies = new ArrayList<>();
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private ServletOutputStream outputStream;
    private PrintWriter writer;

    public LoggingHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void addCookie(Cookie cookie) {
        super.addCookie(cookie);
        cookies.add(cookie);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException("getWriter() has already been called on this response.");
        }

        if (outputStream == null) {
            outputStream = new ServletOutputStream() {
                @Override
                public void write(int b) throws IOException {
                    buffer.write(b);
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(WriteListener listener) {
                    // Do nothing  
                }
            };
        }

        return outputStream;
    }

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

    @Override
    public void flushBuffer() throws IOException {
        if (writer != null) {
            writer.flush();
        } else if (outputStream != null) {
            outputStream.flush();
        }
    }

    public byte[] toByteArray() throws IOException {
        flushBuffer();
        return buffer.toByteArray();
    }
}
