package com.easy.marketgo.common.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/24/22 4:08 PM
 * Describe:
 */
@UtilityClass
public class RandomUtils {

    public String getRandomStr(Integer length) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
