package com.easy.marketgo.common.converter;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-13 11:56:43
 * @description : XStreamMediaIdConverter.java
 */
public class XStreamMediaIdConverter extends XStreamCDataConverter {
    @Override
    public String toString(Object obj) {
        return "<MediaId>" + super.toString(obj) + "</MediaId>";
    }
}
