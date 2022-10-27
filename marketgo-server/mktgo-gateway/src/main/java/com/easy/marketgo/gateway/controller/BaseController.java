package com.easy.marketgo.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 11:03:55
 * @description : BaseController.java
 */

public abstract class BaseController {

    /**
     * 日志类
     */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 空串
     */
    protected static String EMPTY_STR = StringUtils.EMPTY;

    @Autowired(required = false)
    protected ObjectMapper objectMapper;

    /**
     * 获得当前请求对象
     *
     * @return
     */
    protected HttpServletRequest getRequest() {

        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获得当前请求对象
     *
     * @return
     */
    protected HttpServletResponse getResponse() {

        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    /**
     * 添加HttpResponse header
     *
     * @param name
     * @param value
     */
    protected void addHeader(String name, String value) {

        this.getResponse().addHeader(name, value);
    }

    /**
     * 设置HttpResponse header
     *
     * @param name
     * @param value
     */
    protected void setHeader(String name, String value) {

        this.getResponse().setHeader(name, value);
    }

    protected String getHeader(String name) {

        return this.getRequest().getHeader(name);
    }



    protected void writeString(String content) throws Exception {

        HttpServletResponse response = this.getResponse();
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Expires", "0");
        response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Pragma", "no-cache");

        response.getWriter().print(content);
        response.getWriter().close();
    }

    protected void writeJSON(Object object) throws Exception {

        this.writeString(this.objectMapper.writeValueAsString(object));
    }

    protected void writeEmpty() throws Exception {

        this.writeString(EMPTY_STR);
    }
}
