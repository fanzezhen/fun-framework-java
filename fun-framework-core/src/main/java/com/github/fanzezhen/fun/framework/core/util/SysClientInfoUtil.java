package com.github.fanzezhen.fun.framework.core.util;

import com.github.fanzezhen.fun.framework.core.constant.SysConstant;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zezhen.fan
 */
public class SysClientInfoUtil {
    static String unknown = "unknown";
    static String localhostIp = "127.0.0.1";
    static String comma = ",";

    /**
     * 获取发起请求的IP地址
     */
    public static String getIp(HttpServletRequest request) {
        String ip;
        ip = request.getHeader(SysConstant.X_FORWARDED_FOR);
        if ((ip == null) || (ip.length() == 0) || (unknown.equalsIgnoreCase(ip))) {
            ip = request.getHeader(SysConstant.PROXY_CLIENT_IP);
        }
        if ((ip == null) || (ip.length() == 0) || (unknown.equalsIgnoreCase(ip))) {
            ip = request.getHeader(SysConstant.WL_PROXY_CLIENT_IP);
        }
        if ((ip == null) || (ip.length() == 0) || (unknown.equalsIgnoreCase(ip))) {
            ip = request.getRemoteAddr();
            if (localhostIp.equals(ip)) {
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ip = inetAddress != null ? inetAddress.getHostAddress() : null;
            }
        }
        int ipRange = 15;
        if ((ip != null) && (ip.length() > ipRange)) {
            if (ip.indexOf(comma) > 0) {
                ip = ip.substring(0, ip.indexOf(comma));
            }
        }
        return ip;
    }

    /**
     * 获取发起请求的浏览器名称
     */
    public static String getBrowserName(HttpServletRequest request) {
        String header = request.getHeader(SysConstant.USER_AGENT);
        UserAgent userAgent = UserAgent.parseUserAgentString(header);
        Browser browser = userAgent.getBrowser();
        return browser.getName();
    }

    /**
     * 获取发起请求的浏览器版本号
     */
    public static String getBrowserVersion(HttpServletRequest request) {
        String header = request.getHeader(SysConstant.USER_AGENT);
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
        String header = request.getHeader(SysConstant.USER_AGENT);
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
        return new HashMap<>(8) {{
            put("os", SysClientInfoUtil.getOsName(request));
            put("osName", SysClientInfoUtil.getOsName(request));
            put("ip", SysClientInfoUtil.getIp(request));
            put("browserName", SysClientInfoUtil.getBrowserName(request));
            put("browserVersion", SysClientInfoUtil.getBrowserVersion(request));
        }};
    }
}
