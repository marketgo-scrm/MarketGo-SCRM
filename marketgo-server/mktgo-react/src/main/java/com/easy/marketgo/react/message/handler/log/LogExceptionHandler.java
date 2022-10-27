package com.easy.marketgo.react.message.handler.log;

import com.easy.marketgo.react.message.handler.WeErrorExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-15 19:55:47
 * @description : LogExceptionHandler.java
 */
@Slf4j
public class LogExceptionHandler implements WeErrorExceptionHandler {

    @Override
    public void handle(Exception e) {

        log.error("Error happens", e);
    }

}
