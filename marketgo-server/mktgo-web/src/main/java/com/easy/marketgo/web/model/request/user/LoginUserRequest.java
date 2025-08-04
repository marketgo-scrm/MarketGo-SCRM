package com.easy.marketgo.web.model.request.user;

import lombok.Builder;
import lombok.Data;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-05 20:28:25
 * @description : LoginUserRequest.java
 */
@Data
@Builder
public class LoginUserRequest {
//    @PhoneNum(message="手机号码不正确")
    private String userName;
    private String passWord="123456";
}
