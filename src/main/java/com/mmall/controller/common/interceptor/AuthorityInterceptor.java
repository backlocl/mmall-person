package com.mmall.controller.common.interceptor;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) o;
        String methodName = handlerMethod.getMethod().getName();
        String simpleName = handlerMethod.getBean().getClass().getSimpleName();

        if (StringUtils.equals(simpleName,"UserManageController")&&StringUtils.equals(methodName,"login")){
            log.info("请求登录地址 controller:{} method:{}",simpleName,methodName);
            return true;
        }
        StringBuffer stringBuffer = new StringBuffer();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String key : parameterMap.keySet()) {
            String[] arrs = parameterMap.get(key);
            String s = Arrays.toString(arrs);
            stringBuffer.append(key).append("=").append(s);
        }

        String loginToken = CookieUtil.getLoginCookie(request);
        User user = null;
        if (StringUtils.isNotEmpty(loginToken)){
            String userJson = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJson, User.class);
        }
        //用户没有登录或者用户的角色不是管理员
        if (user ==null||(user.getRole()!=Const.Role.ROLE_ADMIN)){
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=utf-8");
            PrintWriter writer = response.getWriter();
            if (user==null){
                writer.write(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截,用户未登录")));
            }else{
                writer.write(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截,用户权限不够")));
            }
            writer.flush();
            writer.close();
            return false;
        }

        log.info("preHandle");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("afterCompletion");
    }
}
