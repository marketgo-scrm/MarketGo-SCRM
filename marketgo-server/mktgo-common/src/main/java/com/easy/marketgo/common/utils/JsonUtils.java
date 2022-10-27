package com.easy.marketgo.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-26 17:35
 * Describe:
 */
public class JsonUtils {

    public static ObjectMapper objectMapper;

    /**
     * 默认日期时间格式
     */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 默认时间格式
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    static {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
        }

        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addSerializer(LocalTime.class,
                new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        objectMapper.registerModule(javaTimeModule);
    }

    public static <T> T toObject(String json, Class<T> valueType) {

        try {
            return objectMapper.readValue(json, valueType);
        } catch (IOException e) {
            throw new RuntimeException("JSON转对象出现异常", e);
        }
    }

    public static <T> T toObject(String json, TypeReference<T> valueTypeRef) {
        try {
            return objectMapper.readValue(json.getBytes(), valueTypeRef);
        } catch (IOException e) {
            throw new RuntimeException("JSON转对象出现异常", e);
        }
    }

    public static <T> T toArray(String json, Class<?> valueType) {

        try {
            JavaType javaType = objectMapper.getTypeFactory()
                    .constructParametricType(ArrayList.class, valueType);
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new RuntimeException("JSON转数组出现异常", e);
        }
    }

    public static String toJSONString(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException("对象转JSON出现异常", e);
        }
    }

    public static JsonNode getNode(String json, String nodeName) {

        try {
            JsonNode node = objectMapper.readTree(json);
            return node.get(nodeName);
        } catch (IOException e) {
            throw new RuntimeException("解析JSON对象节点异常", e);
        }
    }

    public static boolean isJson(String json) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            return jsonNode != null;
        } catch (Throwable e) {
        }
        return false;
    }

    public static JsonNode getNode(String json) {

        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException("解析JSON对象节点异常", e);
        }
    }
}
