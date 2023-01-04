package com.easy.marketgo.web.model.response.user;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-05 20:29:52
 * @description : LoginUserResponse.java
 */
@Data
@Accessors(chain = true)
public class LoginUserResponse  {

    private String userName;
    private String token;
    private String refreshToken;


}
