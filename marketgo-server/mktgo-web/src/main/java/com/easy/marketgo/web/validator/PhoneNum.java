package com.easy.marketgo.web.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-13 22:16:42
 * @description : PhoneNum.java
 */
@Constraint(validatedBy = PhoneNumValidator.class)
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PhoneNum {
    String message() default "手机号码格式不对";
    Class<?> [] groups () default{};
    Class<? extends Payload> [] payload () default{};
}
