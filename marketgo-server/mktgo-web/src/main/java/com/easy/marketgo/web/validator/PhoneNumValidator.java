package com.easy.marketgo.web.validator;

import cn.hutool.core.lang.Validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-13 22:27:51
 * @description : PhoneNumValidator.java
 */
public class PhoneNumValidator implements ConstraintValidator<PhoneNum, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return Validator.isMobile(s);
    }

}
