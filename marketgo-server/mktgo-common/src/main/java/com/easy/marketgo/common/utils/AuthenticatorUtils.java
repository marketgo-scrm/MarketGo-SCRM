package com.easy.marketgo.common.utils;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-05 20:02:42
 * @description : AuthenticatorUtils.java
 */
public class AuthenticatorUtils {

    /**
     * 通用生成密码散列值方法，hashIterations表示散列次数，例如md5(md5(password + salt))，hashIterations=2
     *
     * @param algorithmName
     * @param password
     * @param salt
     * @param hashIterations
     *
     * @return
     */
    public static Hash generateHashValue(String algorithmName, String password, String salt, int hashIterations) {

        return new SimpleHash(algorithmName, password, salt, hashIterations);
    }

    /**
     * 生成md5散列值
     *
     * @param password
     * @param salt
     *
     * @return
     */
    public static String generateMD5HashValue(String password, String salt) {

        Hash hash = AuthenticatorUtils.generateHashValue("MD5", password, salt, 1);
        return hash.toHex();
    }

    /**
     * 生成随机私盐
     *
     * @return
     */
    public static String generateRandomSaltValue() {

        return new SecureRandomNumberGenerator().nextBytes().toHex();
    }
}
