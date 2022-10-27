package com.easy.marketgo.react.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-19 21:05:58
 * @description : WebConfigComponent.java
 */
public class WebConfigComponent implements WebMvcConfigurer {
    @Resource
    private ObjectMapper objectMapper;
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(this.customJackson2HttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        converters.add(new FormHttpMessageConverter());
    }

    /********************************************************
     * 设置jackson转换器
     ********************************************************/
    protected MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(this.objectMapper);
        return jsonConverter;
    }
}
