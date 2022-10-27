package com.easy.marketgo.common.utils;


/**
 * @author :ssk
 * @version :v1.0
 * @date :2019/9/5 11:04
 * @desc :
 */
public class PasswordHelper {

    private static String algorithmName = "md5";

    private static int hashIterations = 1;


    public static void encryptPassword(UserInfo user) {

        String randomSalt = AuthenticatorUtils.generateRandomSaltValue();
        user.setSalt(randomSalt);

        String passwordHashValue = AuthenticatorUtils.generateHashValue(algorithmName, user.getPassword(), getSalt(user), hashIterations).toString();
        user.setPassword(passwordHashValue);
    }

    public static boolean validPassword(UserInfo user, String password) {

        String passwordHashValue = AuthenticatorUtils.generateHashValue(algorithmName, password,
                getSalt(user), hashIterations).toString();
        return user.getPassword().equals(passwordHashValue);
    }

    public static String getSalt(UserInfo user) {

        return user.getUserName() + user.getSalt();


    }

    public static String getEncryptPassword(String password, UserInfo user) {

        return AuthenticatorUtils.generateHashValue(algorithmName, password, getSalt(user), hashIterations).toString();
    }



}
