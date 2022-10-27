package com.easy.marketgo.common.converter;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.thoughtworks.xstream.converters.basic.StringConverter;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-13 11:53:55
 * @description : LongArrayConverter.java
 */
public class LongArrayConverter extends StringConverter {
    @Override
    public boolean canConvert(Class type) {
        return type == Long[].class;
    }

    @Override
    public String toString(Object obj) {
        return "<![CDATA[" + Joiner.on(",").join((Long[]) obj) + "]]>";
    }

    @Override
    public Object fromString(String str) {
        final Iterable<String> iterable = Splitter.on(",").split(str);
        final String[] strings = Iterables.toArray(iterable, String.class);
        Long[] result = new Long[strings.length];
        int index = 0;
        for (String string : strings) {
            result[index++] = Long.parseLong(string);
        }

        return result;
    }
}
