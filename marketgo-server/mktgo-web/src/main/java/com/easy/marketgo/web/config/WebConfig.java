package com.easy.marketgo.web.config;

import com.easy.marketgo.web.interceptor.AuthorizationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthorizationInterceptor authorizationInterceptor;

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //拦截路径可自行配置多个 可用 ，分隔开
        registry
                .addInterceptor(authorizationInterceptor)
                // 拦截路径
                .addPathPatterns("/**")
                // 排除路径
                .excludePathPatterns("/swagger-resources/**",
                        "/webjars/**", "/v2/**", "/swagger-ui.html/**", "doc.html", "/error","resources/**")

        ;

        ;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("swagger-ui.html", "doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/**");

    }

    /**
     * 跨域支持
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 所有的当前站点的请求地址，都支持跨域访问。
        registry.addMapping("/**")
                // 所有的外部域都可跨域访问。 如果是localhost则很难配置，因为在跨域请求的时候，外部域的解析可能是localhost、127.0.0.1、主机名
                .allowedOriginPatterns("*")
                // 是否支持跨域用户凭证
                .allowCredentials(true)
                // 当前站点支持的跨域请求类型是什么
                .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS", "HEAD")
                //超时时长设置
                .maxAge(3600 * 24);
    }


}
