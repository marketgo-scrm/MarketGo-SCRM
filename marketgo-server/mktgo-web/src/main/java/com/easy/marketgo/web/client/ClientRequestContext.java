package com.easy.marketgo.web.client;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 11:23:50
 * @description : ClientRequestContext.java
 */
public class ClientRequestContext implements Serializable {

    private static final long serialVersionUID = 4760019112497309861L;


    private String userName;


    public HttpServletRequest getRequest() {

        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public HttpServletResponse getResponse() {

        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }


    public String getUserName() {

        return this.userName;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

}
