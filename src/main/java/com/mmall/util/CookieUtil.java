package com.mmall.util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
public class CookieUtil {
    private final static String COOKIE_NAME = "mmall_login_token";
    private final static String COOKIE_DOMAIN = ".happymmall.com";


    public static void writeLoginToken(HttpServletResponse response,String token){
        Cookie cookie = new Cookie(COOKIE_NAME,token);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        //时间设置为-1，代表为永久的cookie
        //如果不设置Maxage，那么cookie是不会写入到硬盘的，写到内存中，只会在当前的页面有效
        cookie.setMaxAge(60 * 60 * 24 *365);
        log.info("set cookie CookieName:{} CookieValue:{}",cookie.getName(),cookie.getValue());
        response.addCookie(cookie);
    }

    public static String getLoginCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            log.info("read cookie CookieName:{} CookieValue:{}",cookie.getName(),cookie.getValue());
            if (StringUtils.equals(COOKIE_NAME,cookie.getName())){
                log.info("get cookie CookieName:{} CookieValue:{}",cookie.getName(),cookie.getValue());
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void delLoginCookie(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            log.info("logout get cookie CookieName:{} CookieValue:{}",cookie.getName(),cookie.getValue());
            if (StringUtils.equals(COOKIE_NAME,cookie.getName())){
                log.info("logout cookie CookieName:{} CookieValue:{}",cookie.getName(),cookie.getValue());
                cookie.setDomain(COOKIE_DOMAIN);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }
}
