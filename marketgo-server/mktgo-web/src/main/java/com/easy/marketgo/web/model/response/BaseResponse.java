package com.easy.marketgo.web.model.response;

import com.easy.marketgo.common.enums.ErrorCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 3:41 PM
 * Describe:
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel(description = "请求接口返回的统一response")
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = -8509750561348840506L;
    @ApiModelProperty(value = "错误码")
    private Integer code = null;
    @ApiModelProperty(value = "错误信息")
    private String message = null;
    @ApiModelProperty(value = "业务数据")
    private T data;

    //成功 不返回数据直接返回成功信息
    public static <T> BaseResponse<T> success() {
        BaseResponse<T> response = new BaseResponse<T>();
        response.setCode(ErrorCodeEnum.OK.getCode());
        response.setMessage(ErrorCodeEnum.OK.getMessage());
        return response;
    }

    //成功 并且加上返回数据
    public static  <T>  BaseResponse<T> success(T data) {
        BaseResponse<T> response = new BaseResponse<T>();
        response.setCode(ErrorCodeEnum.OK.getCode());
        response.setMessage(ErrorCodeEnum.OK.getMessage());
        response.setData(data);
        return response;
    }

    //成功 自定义成功返回状态 加上数据
    public static <T> BaseResponse<T> success(ErrorCodeEnum result, T data) {
        BaseResponse<T> response = new BaseResponse<T>();
        response.setCode(result.getCode());
        response.setMessage(result.getMessage());
        response.setData(data);
        return response;
    }

    // 单返回失败的状态码
    public static <T> BaseResponse<T> failure(ErrorCodeEnum result) {
        BaseResponse<T> response = new BaseResponse<T>();
        response.setCode(result.getCode());
        response.setMessage(result.getMessage());
        return response;
    }

    // 单返回失败的状态码
    public static <T> BaseResponse<T> failure(Integer code, String messgae) {
        BaseResponse<T> response = new BaseResponse<T>();
        response.setCode(code);
        response.setMessage(messgae);
        return response;
    }

    // 返回失败的状态码 及 数据
    public static <T> BaseResponse<T> failure(ErrorCodeEnum result, T data) {
        BaseResponse<T> response = new BaseResponse<T>();
        response.setCode(result.getCode());
        response.setMessage(result.getMessage());
        response.setData(data);
        return response;
    }
}
