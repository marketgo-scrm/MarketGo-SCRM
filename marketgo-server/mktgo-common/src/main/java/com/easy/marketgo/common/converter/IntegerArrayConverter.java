package com.easy.marketgo.common.converter;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.thoughtworks.xstream.converters.basic.StringConverter;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-13 11:52:59
 * @description : IntegerArrayConverter.java
 */
public class IntegerArrayConverter extends StringConverter {

    @Override
    public String toString(Object obj) {
        return "<![CDATA[" + Joiner.on(",").join((Integer[]) obj) + "]]>";
    }

    @Override
    public Object fromString(String str) {
        final Iterable<String> iterable = Splitter.on(",").split(str);
        final String[] strings = Iterables.toArray(iterable, String.class);
        Integer[] result = new Integer[strings.length];
        int index = 0;
        for (String string : strings) {
            result[index++] = Integer.parseInt(string);
        }

        return result;
    }
}
