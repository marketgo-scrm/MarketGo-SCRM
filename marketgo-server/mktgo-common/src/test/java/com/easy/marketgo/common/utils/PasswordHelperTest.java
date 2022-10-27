package com.easy.marketgo.common.utils;

import org.junit.Assert;
import org.junit.Test;

public class PasswordHelperTest {
    @Test()
    public void validPasswordTest(){

        UserInfo user = UserInfo.builder()
                                .userName("18201552650")
                                .password("123456")
                                .salt("")
                                .build();


        PasswordHelper.encryptPassword(user);
        Assert.assertTrue(PasswordHelper.validPassword(user, "123456"));
        System.out.println(user.getPassword());
        System.out.println(PasswordHelper.getEncryptPassword("1234567",user));
        Assert.assertNotEquals(user.getPassword(),PasswordHelper.getEncryptPassword("1234567",user));
    }
}
