package com.easy.marketgo.web.exception;

import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.core.model.bo.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/29/22 11:37 AM
 * Describe:
 */
@RestControllerAdvice(basePackages = "com.easy.marketgo.web.controller")
@Component
@Slf4j
public class WebExceptionHandler {

    /**
     * validation参数校验异常 统一处理
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public ResponseEntity<BaseResponse> exceptionHandler500(BindException e) {

        e.printStackTrace();
        StringBuilder stringBuilder = new StringBuilder();
        for (ObjectError error : e.getAllErrors()) {
            stringBuilder.append("[");
            stringBuilder.append(((FieldError) error).getField());
            stringBuilder.append(" ");
            stringBuilder.append(error.getDefaultMessage());
            stringBuilder.append("]");
        }
        return ResponseEntity.ok(
                BaseResponse.builder()
                            .code(ErrorCodeEnum.ERROR_WEB_MASS_TASK_IS_EMPTY.getCode())
                            .message("【参数校验失败】 " + stringBuilder.toString()).build());
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<BaseResponse> exceptionHandler500(ConstraintViolationException e) {

        e.printStackTrace();
        StringBuilder stringBuilder = new StringBuilder();
        for (ConstraintViolation<?> error : e.getConstraintViolations()) {
            PathImpl pathImpl = (PathImpl) error.getPropertyPath();
            String paramName = pathImpl.getLeafNode().getName();
            stringBuilder.append("[");
            stringBuilder.append(paramName);
            stringBuilder.append(" ");
            stringBuilder.append(error.getMessage());
            stringBuilder.append("]");
        }
        return ResponseEntity.ok(
                BaseResponse.builder()
                            .code(ErrorCodeEnum.ERROR_WEB_MASS_TASK_IS_EMPTY.getCode())
                            .message("【参数校验失败】 " + stringBuilder.toString()).build());

    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<BaseResponse> exceptionHandler(Exception e) {


        log.error("caught exception", e);
        if (e instanceof CommonException) {
            return ResponseEntity.ok(
                    BaseResponse.builder().code(((CommonException) e).getCode()).message(((CommonException) e).getMessage()).build()
            );
        }
        return ResponseEntity.ok(
                BaseResponse.builder()
                            .code(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE.getCode())
                            .message(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE.getMessage())
                            .build()
        );
    }

}

