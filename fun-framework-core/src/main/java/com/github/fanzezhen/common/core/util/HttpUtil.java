package com.github.fanzezhen.fun.framework.core.util;

import cn.stylefeng.roses.kernel.model.response.ResponseData;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author fanzezhen
 */
@Slf4j
public class HttpUtil {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        // 发送同步请求：
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.codesheep.cn"))
                .GET()
                .build();
        // 同步请求方式，拿到结果前会阻塞当前线程
        var httpResponse = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
        log.debug("拿到结果前会阻塞当前线程...");
        // 打印获取到的网页内容
        log.debug(httpResponse.body());

        // 发送异步请求：
        CompletableFuture<String> future = HttpClient.newHttpClient().
                sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        log.debug("先继续干点别的事情...");
        // // 打印获取到的网页内容
        log.debug(future.get());
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String ajaxFlag = request.getHeader("X-Requested-With");
        if (ajaxFlag != null) {
            return "XMLHttpRequest".equals(ajaxFlag);
        } else {
            return false;
        }
    }

    /**
     * POST请求
     */
    public static ResponseData doPost(String url, Map<String, String> params) {
        ResponseData result;
        OutputStream out = null;
        DataOutputStream dataOutputStream = null;
        InputStream in = null;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();) {
            URLConnection urlConnection = new URL(url).openConnection();
            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
            // 设置是否向httpUrlConnection输出，post请求，参数要放在http正文内，因此需要设为true,
            httpUrlConnection.setDoOutput(true);
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            httpUrlConnection.setDoInput(true);
            // 忽略缓存
            httpUrlConnection.setUseCaches(false);
            // 设定请求的方法为"POST"，默认是GET
            httpUrlConnection.setRequestMethod(HttpMethod.POST.name());
            httpUrlConnection.connect();

            // 建立输入流，向指向的URL传入参数

            StringBuilder queryString = getQueryString(params);

            if (queryString.length() > 0) {
                queryString = new StringBuilder(queryString.substring(0, queryString.length() - 1));
                out = httpUrlConnection.getOutputStream();
                dataOutputStream = new DataOutputStream(out);
                dataOutputStream.writeBytes(queryString.toString());

                dataOutputStream.flush();
                out.flush();
            }

            // 获得响应状态
            int responseCode = httpUrlConnection.getResponseCode();

            if (HttpURLConnection.HTTP_OK == responseCode) {
                int len;
                in = httpUrlConnection.getInputStream();
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, len);
                    byteArrayOutputStream.flush();
                }
                result = ResponseData.success(byteArrayOutputStream.toString(StandardCharsets.UTF_8));
            } else {
                result = ResponseData.error(responseCode, "请求异常");
            }
        } catch (IOException e) {
            result = ResponseData.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getLocalizedMessage());
        } finally {
            if (closeStream(in) && closeStream(out) && closeStream(dataOutputStream)) {
                result = ResponseData.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求关闭异常");
            }
        }
        return result;
    }

    /**
     * GET请求
     */
    public static ResponseData doGet(String url, Map<String, String> params) {
        ResponseData result;

        InputStream in = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();) {
            // URL传入参数
            StringBuilder queryString = getQueryString(params);

            if (queryString.length() > 0) {
                queryString = new StringBuilder(queryString
                        .substring(0, queryString.length() - 1));

                url = url + "?" + queryString;
            }

            URLConnection urlConnection = new URL(url).openConnection();
            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
            // 设置是否向httpUrlConnection输出，post请求，参数要放在http正文内，因此需要设为true,
            httpUrlConnection.setDoOutput(false);
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            httpUrlConnection.setDoInput(true);
            // 忽略缓存
            httpUrlConnection.setUseCaches(false);
            // 设定请求的方法为"POST"，默认是GET
            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.connect();

            // 获得响应状态
            int responseCode = httpUrlConnection.getResponseCode();

            if (HttpURLConnection.HTTP_OK == responseCode) {
                byte[] buffer = new byte[1024];
                int len;
                in = httpUrlConnection.getInputStream();
                while ((len = in.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, len);
                    byteArrayOutputStream.flush();
                }
                result = ResponseData.success(byteArrayOutputStream.toString(StandardCharsets.UTF_8));
            } else {
                result = ResponseData.error(responseCode, "请求异常");
            }
        } catch (IOException e) {
            result = ResponseData.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getLocalizedMessage());
        } finally {
            if (closeStream(in)) {
                result = ResponseData.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求关闭异常");
            }
        }

        return result;
    }

    private static StringBuilder getQueryString(Map<String, String> params) {
        StringBuilder queryString = new StringBuilder();

        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                queryString.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(),
                        StandardCharsets.UTF_8)).append("&");
            }
        }
        return queryString;
    }

    private static boolean closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                log.error(e.toString());
                return false;
            }
        }
        return true;
    }
}
