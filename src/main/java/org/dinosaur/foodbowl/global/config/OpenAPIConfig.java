package org.dinosaur.foodbowl.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    private final String devUrl;
    ;

    public OpenAPIConfig(@Value("${openapi.dev_url}") String devUrl) {
        this.devUrl = devUrl;
    }

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Foodbowl API")
                .version("v1.0.0")
                .description("푸드보울 API");

        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.description("개발 환경 서버 URL");

        SecurityScheme securityScheme = new SecurityScheme()
                .type(Type.HTTP)
                .in(In.HEADER)
                .name("Authorization")
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Bearer JWT");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("BearerAuth");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer))
                .components(new Components().addSecuritySchemes("BearerAuth", securityScheme))
                .security(List.of(securityRequirement));
    }
}
