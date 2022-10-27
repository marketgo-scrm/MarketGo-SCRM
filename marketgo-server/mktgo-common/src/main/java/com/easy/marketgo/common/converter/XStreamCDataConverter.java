package com.easy.marketgo.common.converter;

import com.thoughtworks.xstream.converters.basic.StringConverter;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-13 11:51:29
 * @description : XStreamCDataConverter.java
 */
public class XStreamCDataConverter extends StringConverter {

    @Override
    public String toString(Object obj) {

        return "<![CDATA[" + super.toString(obj) + "]]>";
    }

}
