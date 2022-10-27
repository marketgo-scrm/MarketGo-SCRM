package com.easy.marketgo.web.model.request.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-05 20:28:25
 * @description : LoginUserRequest.java
 */
@Data
@Builder
public class UserChangePasswordRequest {

    //    @PhoneNum(message="手机号码不正确")
    @ApiModelProperty(name = "userName", notes = "用户名", example = "")
    @NotEmpty(message = "userName不能为空")
    private String userName;
    @ApiModelProperty(name = "passWord", notes = "密码", example = "")
    @NotEmpty(message = "密码不能为空")
    private String passWord = "123456";
    @ApiModelProperty(name = "changePassWord", notes = "修改后密码", example = "")
    @NotEmpty(message = "修改后密码不能为空")
    private String changePassWord;
    @ApiModelProperty(name = "recChangePassWord", notes = "重复输入后修改后密码", example = "")
    @NotEmpty(message = "重复输入后修改后密码密码")
    private String recChangePassWord;

}
