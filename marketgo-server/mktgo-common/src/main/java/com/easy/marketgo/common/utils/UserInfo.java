package com.easy.marketgo.common.utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {


    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 掩码
     */
    private String salt;

}
