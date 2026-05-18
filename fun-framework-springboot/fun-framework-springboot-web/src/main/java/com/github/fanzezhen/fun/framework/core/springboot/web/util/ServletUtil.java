package com.github.fanzezhen.fun.framework.core.springboot.web.util;

import cn.hutool.core.map.MapUtil;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.model.constant.ServletConstant;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.ExceptionCodeEnum;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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
 * Servlet工具类.
 * <p>
 * 提供HTTP请求相关的工具方法，包括IP地址获取、浏览器信息解析、
 * 文件下载等常用功能。
 */
@Slf4j
public class ServletUtil {
    /**
     * 未知标识.
     */
    private static final String UNKNOWN = "unknown";

    /**
     * 本地回环地址.
     */
    private static final String LOCALHOST_IP = "127.0.0.1";

    /**
     * IP地址长度阈值（IPv4最大15位，超过此值说明可能包含代理链）.
     */
    private static final int IP_LENGTH_THRESHOLD = 15;

    /**
     * 逗号分隔符.
     */
    private static final String COMMA_SEPARATOR = ",";

    /**
     * 请求参数Map初始容量.
     */
    private static final int PARAM_MAP_INITIAL_CAPACITY = 8;

    /**
     * 文件缓冲区大小（1KB）.
     */
    private static final int FILE_BUFFER_SIZE = 1024;

    /**
     * 私有构造函数，防止实例化.
     */
    private ServletUtil() {
    }

    /**
     * 获取发起请求的真实IP地址（支持代理场景）.
     * <p>
     * 代理头优先级顺序：
     * <ol>
     *     <li>X-Forwarded-For - 标准HTTP代理头，格式为"客户端IP, 代理1, 代理2"，取第一个IP</li>
     *     <li>Proxy-Client-IP - Apache服务器代理头</li>
     *     <li>WL-Proxy-Client-IP - WebLogic服务器代理头</li>
     *     <li>request.getRemoteAddr() - 直连场景或所有代理头为空时的兜底方案</li>
     * </ol>
     * <p>
     * 优先级基于实际部署中的常见架构（Nginx → Spring Boot），
     * X-Forwarded-For最常见且最接近真实客户端IP。
     *
     * @param request HTTP请求对象
     * @return 客户端真实IP地址
     */
    public static String getIp(final HttpServletRequest request) {
        String ip = getHeader(request, ServletConstant.X_FORWARDED_FOR);
        if (isUnknown(ip)) {
            ip = getHeader(request, ServletConstant.PROXY_CLIENT_IP);
        }
        if (isUnknown(ip)) {
            ip = getHeader(request, ServletConstant.WL_PROXY_CLIENT_IP);
        }
        if (isUnknown(ip)) {
            ip = request.getRemoteAddr();
            if (LOCALHOST_IP.equals(ip)) {
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    log.warn("", e);
                }
                ip = inetAddress != null ? inetAddress.getHostAddress() : null;
            }
        }
        if (ip != null && ip.length() > IP_LENGTH_THRESHOLD && ip.contains(COMMA_SEPARATOR)) {
            ip = ip.substring(0, ip.indexOf(COMMA_SEPARATOR));
        }
        return ip;
    }

    /**
     * 获取请求头.
     *
     * @param request HTTP请求对象
     * @param headerName 请求头名称
     * @return 请求头值，不存在或为unknown时返回null
     */
    public static String getHeader(final HttpServletRequest request, final String headerName) {
        String ip = request.getHeader(headerName);
        return isUnknown(ip) ? null : ip;
    }

    /**
     * 判断IP地址是否为unknown.
     *
     * @param ip IP地址字符串
     * @return true表示IP无效或为unknown
     */
    public static boolean isUnknown(final String ip) {
        return ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip);
    }

    /**
     * 获取发起请求的浏览器名称.
     *
     * @param request HTTP请求对象
     * @return 浏览器名称
     */
    public static String getBrowserName(final HttpServletRequest request) {
        String header = request.getHeader(ServletConstant.USER_AGENT);
        UserAgent userAgent = UserAgent.parseUserAgentString(header);
        Browser browser = userAgent.getBrowser();
        return browser.getName();
    }

    /**
     * 获取发起请求的浏览器版本号.
     *
     * @param request HTTP请求对象
     * @return 浏览器版本号
     */
    public static String getBrowserVersion(final HttpServletRequest request) {
        String header = request.getHeader(ServletConstant.USER_AGENT);
        UserAgent userAgent = UserAgent.parseUserAgentString(header);
        // 获取浏览器信息
        Browser browser = userAgent.getBrowser();
        // 获取浏览器版本号
        Version version = browser.getVersion(header);
        return version.getVersion();
    }

    /**
     * 获取发起请求的操作系统名称.
     *
     * @param request HTTP请求对象
     * @return 操作系统名称
     */
    public static String getOsName(final HttpServletRequest request) {
        String header = request.getHeader(ServletConstant.USER_AGENT);
        UserAgent userAgent = UserAgent.parseUserAgentString(header);
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();
        return operatingSystem.getName();
    }

    /**
     * 获取当前请求的IP地址.
     *
     * @return IP地址，无请求上下文时返回127.0.0.1
     */
    public static String getIp() {
        HttpServletRequest request = getRequest();
        return request == null ? LOCALHOST_IP : request.getRemoteHost();
    }

    /**
     * 获取当前请求对象.
     *
     * @return HTTP请求对象，不存在时返回null
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : requestAttributes.getRequest();
    }

    /**
     * 获取当前响应对象.
     *
     * @return HTTP响应对象，不存在时返回null
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : requestAttributes.getResponse();
    }

    /**
     * 获取请求参数.
     *
     * @return 参数Map，key为参数名，value为参数值
     */
    public static Map<String, String> getRequestParameters() {
        HashMap<String, String> values = HashMap.newHashMap(PARAM_MAP_INITIAL_CAPACITY);
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

    /**
     * 获取请求的所有信息（操作系统、IP、浏览器等）.
     *
     * @param request HTTP请求对象
     * @return 信息Map
     */
    public static Map<String, String> getAll(final HttpServletRequest request) {
        return MapUtil.builder(new HashMap<String, String>())
            .put("os", ServletUtil.getOsName(request))
            .put("osName", ServletUtil.getOsName(request))
            .put("ip", ServletUtil.getIp(request))
            .put("browserName", ServletUtil.getBrowserName(request))
            .put("browserVersion", ServletUtil.getBrowserVersion(request))
            .build();
    }

    /**
     * 将文件作为响应返回（下载文件）.
     *
     * @param response HTTP响应对象
     * @param file 待下载的文件
     * @throws ServiceException 文件不存在或IO异常时抛出
     */
    public static void response(final HttpServletResponse response, final File file) {
        // 清空输出流
        response.reset();
        // 设置强制下载不打开
        response.setContentType("application/force-download");
        // 设置文件名
        response.addHeader("Content-Disposition", "attachment;fileName="
            + new String(file.getName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        byte[] buffer = new byte[FILE_BUFFER_SIZE];
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
            throw new ServiceException(ExceptionCodeEnum.NOT_FOUND);
        } catch (IOException e) {
            throw new ServiceException("输出流读取失败！");
        }
    }

}
