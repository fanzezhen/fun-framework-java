package com.github.fanzezhen.fun.framework.core.data;

import cn.hutool.core.map.MapUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fanzezhen
 */
@Slf4j
public class ServletUtil {
    private ServletUtil() {
    }

    static String unknown = "unknown";
    static String localhostIp = "127.0.0.1";

    /**
     * 获取发起请求的IP地址
     */
    public static String getIp(HttpServletRequest request) {
        String ip = getHeader(request, ServletConstant.X_FORWARDED_FOR);
        if (isUnknown(ip)) {
            ip = getHeader(request, ServletConstant.PROXY_CLIENT_IP);
        }
        if (isUnknown(ip)) {
            ip = getHeader(request, ServletConstant.WL_PROXY_CLIENT_IP);
        }
        if (isUnknown(ip)) {
            ip = request.getRemoteAddr();
            if (localhostIp.equals(ip)) {
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    log.warn("", e);
                }
                ip = inetAddress != null ? inetAddress.getHostAddress() : null;
            }
        }
        if (ip != null && ip.length() > 15 && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }

    private static String getHeader(HttpServletRequest request, String headerName) {
        String ip = request.getHeader(headerName);
        return isUnknown(ip) ? null : ip;
    }

    private static boolean isUnknown(String ip) {
        return ip == null || ip.isEmpty() || unknown.equalsIgnoreCase(ip);
    }

    /**
     * 获取发起请求的浏览器名称
     */
    public static String getBrowserName(HttpServletRequest request) {
        String header = request.getHeader(ServletConstant.USER_AGENT);
        UserAgent userAgent = UserAgent.parseUserAgentString(header);
        Browser browser = userAgent.getBrowser();
        return browser.getName();
    }

    /**
     * 获取发起请求的浏览器版本号
     */
    public static String getBrowserVersion(HttpServletRequest request) {
        String header = request.getHeader(ServletConstant.USER_AGENT);
        UserAgent userAgent = UserAgent.parseUserAgentString(header);
        //获取浏览器信息
        Browser browser = userAgent.getBrowser();
        //获取浏览器版本号
        Version version = browser.getVersion(header);
        return version.getVersion();
    }

    /**
     * 获取发起请求的操作系统名称
     */
    public static String getOsName(HttpServletRequest request) {
        String header = request.getHeader(ServletConstant.USER_AGENT);
        UserAgent userAgent = UserAgent.parseUserAgentString(header);
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();
        return operatingSystem.getName();
    }


    public static String getIp() {
        HttpServletRequest request = getRequest();
        return request == null ? "127.0.0.1" : request.getRemoteHost();
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : requestAttributes.getRequest();
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : requestAttributes.getResponse();
    }

    public static Map<String, String> getRequestParameters() {
        HashMap<String, String> values = new HashMap<>(8);
        HttpServletRequest request = getRequest();
        if (request != null) {
            Enumeration<String> enums = request.getParameterNames();

            while (enums.hasMoreElements()) {
                String paramName = enums.nextElement();
                String paramValue = request.getParameter(paramName);
                values.put(paramName, paramValue);
            }

        }
        return values;
    }

    public static Map<String, String> getAll(HttpServletRequest request) {
        return MapUtil.builder(new HashMap<String, String>())
            .put("os", ServletUtil.getOsName(request)).
            put("osName", ServletUtil.getOsName(request)).
            put("ip", ServletUtil.getIp(request)).
            put("browserName", ServletUtil.getBrowserName(request)).
            put("browserVersion", ServletUtil.getBrowserVersion(request))
            .build();
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
        } catch (FileNotFoundException e) {
            throw new ServiceException(CoreExceptionEnum.FILE_NOT_FOUND);
        } catch (IOException e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "输出流读取失败！");
        }
    }

}
