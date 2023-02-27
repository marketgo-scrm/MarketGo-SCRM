package com.easy.marketgo.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/19/22 11:02 AM
 * Describe:
 */
@EnableOpenApi
@Configuration
public class SwaggerConfig {

    /**
     * 配置基本信息
     * @return
     */
    @Bean
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger Test App Restful API")
                .description("swagger test app restful api")
                .termsOfServiceUrl("")
                .contact(new Contact("ssk","http://127.0.0.1","shang.shi.kun@hotmail.com"))
                .version("1.0")
                .build();
    }
    /**
     * 配置文档生成最佳实践
     * @param apiInfo
     * @return
     */
    @Bean
    public Docket createRestApi(ApiInfo apiInfo) {

        List<RequestParameter> requestParameters = new ArrayList<>() ;

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .groupName("WebOpenAPI")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.easy.marketgo.web"))
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(requestParameters)
                ;
    }



}