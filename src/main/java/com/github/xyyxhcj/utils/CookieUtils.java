package com.github.xyyxhcj.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Cookie工具类
 *
 * @author xyyxhcj@qq.com
 * @since 2018/2/19
 */
public final class CookieUtils {
    /**
     * 返回cookieName对应的值(UTF-8)
     * @param request request
     * @param cookieName cookieName
     * @param isDecoder isDecoder
     * @return return
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName, boolean isDecoder) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookieName == null) {
            return null;
        }
        String value = null;
        try {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    if (isDecoder) {
                        value = URLDecoder.decode(cookie.getValue(), "UTF-8");
                    } else {
                        value = cookie.getValue();
                    }
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 返回对应的cookie值,不编码
     * @param request request
     * @param cookieName cookieName
     * @return return
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        return getCookieValue(request, cookieName, false);
    }

    /**
     * 返回对应编码的cookie值
     * @param request request
     * @param cookieName cookieName
     * @param encodeString encodeString
     * @return return
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName, String encodeString) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookieName == null) {
            return null;
        }
        String value=null;
        try {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    value = URLDecoder.decode(cookie.getValue(), encodeString);
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取cookie的域名
     * @param request request
     * @return return
     */
    private static String getDomainName(HttpServletRequest request) {
        String domainName;
        String serverName = request.getRequestURL().toString();
        if ("".equals(serverName.trim())) {
            domainName = "";
        } else {
            serverName = serverName.toLowerCase().substring(serverName.indexOf("/")+2);
            final int end = serverName.indexOf("/");
            serverName = serverName.substring(0, end);
            final String[] domains = serverName.split("\\.");
            int len = domains.length;
            if (len > 3) {
                //www.xxx.com.cn
                domainName = "." + domains[len - 3] + "." + domains[len - 2] + "." + domains[len - 1];
            } else if (len > 1) {
                //xxx.com or xxx.cn
                domainName = "." + domains[len - 2] + "." + domains[len - 1];
            } else {
                domainName = serverName;
            }
        }
        //过滤端口号
        if (domainName.indexOf(":") > 0) {
            domainName = domainName.split(":")[0];
        }
        return domainName;
    }

    /**
     * 当request不为空时设置cookie的域名
     * @param request request
     * @param cookie cookie
     */
    private static void setDomain(HttpServletRequest request, Cookie cookie) {
        if (null != request) {
            String domainName=getDomainName(request);
            System.out.println(domainName);
            if (!"localhost".equals(domainName)) {
                cookie.setDomain(domainName);
            }
        }
    }

    /**
     * 设置Cookie的值,并使其在指定时间内生效
     * @param request 用于设置域名的cookie(为null时表示不设置域名cookie)
     * @param cookieMaxAge cookie有效期(秒)
     */
    private static void doSetCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxAge, boolean isEncode) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            } else if(isEncode) {
                cookieValue = URLEncoder.encode(cookieValue, "utf-8");
            }
            Cookie cookie = new Cookie(cookieName, cookieValue);
            if (cookieMaxAge > 0) {
                cookie.setMaxAge(cookieMaxAge);
            }
            //设置域名的cookie
            setDomain(request, cookie);
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置cookie的值(指定有效期,单位:秒)
     * @param request request
     * @param response response
     * @param cookieName cookieName
     * @param cookieValue cookieValue
     * @param cookieMaxAge cookieMaxAge
     * @param isEncode isEncode
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxAge, boolean isEncode) {
        doSetCookie(request,response,cookieName,cookieValue,cookieMaxAge,isEncode);
    }
    /**
     * 设置 Cookie的值(指定有效期),不编码
     * @param request request
     * @param response response
     * @param cookieName cookieName
     * @param cookieValue cookieValue
     * @param cookieMaxAge 有效期
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxAge) {
        setCookie(request,response,cookieName,cookieValue,cookieMaxAge,false);
    }

    /**
     * 设置Cookie的值,使用默认有效期(浏览器关闭即失效),不编码
     * @param request request
     * @param response response
     * @param cookieName cookieName
     * @param cookieValue cookieValue
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue) {
        setCookie(request,response,cookieName,cookieValue,-1);
    }

    /**
     * 设置Cookie的值,不设置有效期,编码
     * @param request request
     * @param response response
     * @param cookieName cookieName
     * @param cookieValue cookieValue
     * @param isEncode isEncode
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, boolean isEncode) {
        setCookie(request,response,cookieName,cookieValue,-1,isEncode);
    }

    /**
     * 设置Cookie值时指定编码参数
     * @param request request
     * @param response response
     * @param cookieName cookieName
     * @param cookieValue cookieValue
     * @param cookieMaxAge cookieMaxAge
     * @param encodeString encodeString
     */
    private static void doSetCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxAge, String encodeString) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            }else {
                cookieValue = URLEncoder.encode(cookieValue, encodeString);
            }
            Cookie cookie = new Cookie(cookieName, cookieValue);
            if (cookieMaxAge > 0) {
                cookie.setMaxAge(cookieMaxAge);
            }
            //设置域名
            setDomain(request, cookie);
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置Cookie的值(指定有效期及编码参数)
     * @param request request
     * @param response response
     * @param cookieName cookieName
     * @param cookieValue cookieValue
     * @param cookieMaxAge cookieMaxAge
     * @param encodeString encodeString
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxAge, String encodeString) {
        doSetCookie(request,response,cookieName,cookieValue,cookieMaxAge,encodeString);
    }

    /**
     * 删除Cookie
     * @param request request
     * @param response response
     * @param cookieName cookieName
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        doSetCookie(request,response,cookieName,"",-1,false);
    }
}
