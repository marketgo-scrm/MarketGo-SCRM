package com.easy.marketgo.web.client;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 11:23:45
 * @description : ClientRequestContextHolder.java
 */
public abstract class ClientRequestContextHolder {

    private final static ThreadLocal<ClientRequestContext> clientRequestContextThreadLocal = new ThreadLocal<>();

    public static ClientRequestContext current() {

        ClientRequestContext clientRequestContext = clientRequestContextThreadLocal.get();
        if (clientRequestContext == null) {
            clientRequestContext = new ClientRequestContext();
            clientRequestContextThreadLocal.set(clientRequestContext);
        }
        return clientRequestContext;
    }

    public static void reset() {

        clientRequestContextThreadLocal.remove();
    }
}
