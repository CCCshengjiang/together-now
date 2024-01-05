package com.wen.togethernow.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义Swagger主页信息
 *
 * @author wen
 */
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI swaggerOpenApi() {
        return new OpenAPI()
                .info(new Info().title("together-now")
                        .description("帮助大家找到志同道合的伙伴的移动端H5网页")
                        .version("v1.0.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("作者信息")
                        .url("https://github.com/CCCshengjiang"));
    }
}