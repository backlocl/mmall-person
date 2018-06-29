package com.mmall.controller.common;

import com.mmall.common.Const;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SessionExpireFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;

        String sessionId = CookieUtil.getLoginCookie(request);

        if (StringUtils.isNotEmpty(sessionId)){

            String userJsonstr = RedisShardedPoolUtil.get(sessionId);

            User user = JsonUtil.string2Obj(userJsonstr, User.class);

            if (user!=null){
                RedisShardedPoolUtil.expire(sessionId,Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
            }
        }


        filterChain.doFilter(servletRequest,servletResponse);

    }

    @Override
    public void destroy() {

    }
}
