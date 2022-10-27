package com.easy.marketgo.common.exception;

import com.easy.marketgo.common.enums.ErrorCodeEnum;
import lombok.Getter;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/29/22 4:23 PM
 * Describe:
 */
@Getter
public class CommonException extends RuntimeException {
    private Integer code;
    private String message;

    public CommonException(String message) {
        super(message);
        this.message = message;
        this.code = ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE.getCode();
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE.getCode();
    }

    public CommonException(Integer code, String message) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public CommonException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = code;
    }

    public CommonException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMessage());
        this.message = errorCodeEnum.getMessage();
        this.code = errorCodeEnum.getCode();
    }
}
