package com.easy.marketgo.web.interceptor;

import cn.hutool.core.util.StrUtil;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.core.entity.WeComSysUserEntity;
import com.easy.marketgo.core.redis.RedisService;
import com.easy.marketgo.core.repository.user.WeComSysUserRepository;
import com.easy.marketgo.web.annotation.TokenIgnore;
import com.easy.marketgo.web.client.ClientRequestContextHolder;
import com.easy.marketgo.web.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 10:34:24
 * @description : AuthorizationInterceptor.java
 */
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {


    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private WeComSysUserRepository sysUserRepository;

    public static final String USER_KEY = "sys_username";
    @Autowired
    private RedisService redisService;
    private static  final  String USER_TOKEN_KEY="marketgo_user_token_%s";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 初始化
        ClientRequestContextHolder.reset();

        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }
        // 忽略带JwtIgnore注解的请求, 不做后续token认证校验
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            TokenIgnore jwtIgnore = handlerMethod.getMethodAnnotation(TokenIgnore.class);
            if (jwtIgnore != null) {
                return true;
            }
        }

        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        //获取用户凭证
        String token = request.getHeader(jwtUtils.getApiToken());
        if (StrUtil.isBlank(token)) {
            token = request.getParameter(jwtUtils.getApiToken());
        }

//        凭证为空
        if (StrUtil.isBlank(token)) {
            throw new CommonException(HttpStatus.UNAUTHORIZED.value(), "token 不能为空");
        }

        boolean exists = redisService.exists(String.format(USER_TOKEN_KEY, token));
        if (!exists){
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_TOKEN_IS_EXPIRED);
        }

        String userName = jwtUtils.getUsername(token);
        if (StrUtil.isBlank(userName)) {
            throw new CommonException(HttpStatus.NOT_ACCEPTABLE.value(), "没有找到对应的用户名");
        }
        if (jwtUtils.isExpired(token)) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_TOKEN_IS_EXPIRED);
        }
        WeComSysUserEntity userEntity = sysUserRepository.queryByUserName(userName);
        if (Objects.isNull(userEntity)) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_USER_IS_NOT_EXISTS);
        }

        //设置userId到request里，后续根据userId，获取用户信息
        ClientRequestContextHolder.current()
                                  .setUserName(userName);
        ClientRequestContextHolder.current().getRequest().setAttribute(USER_KEY, userName);
        redisService.expire(String.format(USER_TOKEN_KEY, token),6, TimeUnit.HOURS);
        return true;
    }

    /**
     * Callback after completion of request processing, that is, after rendering
     * the view. Will be called on any outcome of handler execution, thus allows
     * for proper resource cleanup.
     * <p>Note: Will only be called if this interceptor's {@code preHandle}
     * method has successfully completed and returned {@code true}!
     * <p>As with the {@code postHandle} method, the method will be invoked on each
     * interceptor in the chain in reverse order, so the first interceptor will be
     * the last to be invoked.
     * <p><strong>Note:</strong> special considerations apply for asynchronous
     * request processing. For more details see
     * {@link AsyncHandlerInterceptor}.
     * <p>The default implementation is empty.
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  the handler (or {@link HandlerMethod}) that started asynchronous
     *                 execution, for type and/or instance examination
     * @param ex       any exception thrown on handler execution, if any; this does not
     *                 include exceptions that have been handled through an exception resolver
     * @throws Exception in case of errors
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        ClientRequestContextHolder.reset();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

}
