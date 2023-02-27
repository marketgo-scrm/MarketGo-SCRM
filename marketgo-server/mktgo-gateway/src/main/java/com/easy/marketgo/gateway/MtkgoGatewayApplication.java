package com.easy.marketgo.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableOpenApi
@SpringBootApplication(scanBasePackages = {"com.easy.marketgo"})
@ImportResource(value = "classpath:dubbo-provider.xml")
@EnableCaching
@EnableJdbcRepositories(value = {"com.easy.marketgo"})
@EntityScan("com.easy.marketgo")
public class MtkgoGatewayApplication {

    public static void main(String[] args) {
        try {
            System.setProperty("zookeeper.sasl.client", "false");
            SpringApplication.run(MtkgoGatewayApplication.class, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
