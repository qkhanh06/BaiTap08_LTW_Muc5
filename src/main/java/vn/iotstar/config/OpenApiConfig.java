package vn.iotstar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI appOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Category & Product REST API")
                .version("1.0")
                .description("Spring Boot 3 CRUD API, search, pagination and AJAX demo"));
    }
}
