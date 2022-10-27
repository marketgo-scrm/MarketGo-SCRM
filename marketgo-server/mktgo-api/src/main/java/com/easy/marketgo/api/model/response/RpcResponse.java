package com.easy.marketgo.api.model.response;

import com.easy.marketgo.common.enums.ErrorCodeEnum;

import java.io.Serializable;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-27 19:20
 * Describe:
 */
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = 4375938388290324857L;
    /**
     * 是否响应成功
     */
    private Boolean success;
    /**
     * 响应状态码
     */
    private Integer code;
    /**
     * 响应数据
     */
    private T data;
    /**
     * 错误信息
     */
    private String message;

    // 构造器开始

    /**
     * 无参构造器(构造器私有，外部不可以直接创建)
     */
    private RpcResponse() {
        this.code = 0;
        this.success = true;
    }

    /**
     * 有参构造器
     *
     * @param obj
     */
    private RpcResponse(T obj) {
        this.code = 0;
        this.data = obj;
        this.success = true;
    }

    /**
     * 有参构造器
     *
     * @param resultCode
     */
    private RpcResponse(ErrorCodeEnum resultCode) {
        this.success = false;
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
    // 构造器结束

    /**
     * 通用返回成功（没有返回结果）
     *
     * @param <T>
     * @return
     */
    public static <T> RpcResponse<T> success() {
        return new RpcResponse();
    }

    /**
     * 返回成功（有返回结果）
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> RpcResponse<T> success(T data) {
        return new RpcResponse<T>(data);
    }

    /**
     * 通用返回失败
     *
     * @param resultCode
     * @param <T>
     * @return
     */
    public static <T> RpcResponse<T> failure(ErrorCodeEnum resultCode) {
        return new RpcResponse<T>(resultCode);
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "RpcResult{" +
                "success=" + success +
                ", code=" + code +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}
