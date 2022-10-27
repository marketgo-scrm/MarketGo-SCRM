package com.easy.marketgo.web.model.response.user;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class LogoutUserResponse {

    private String userName;



}
