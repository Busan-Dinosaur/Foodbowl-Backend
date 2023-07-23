package org.dinosaur.foodbowl.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    private final String devUrl;
    private final String prodUrl;

    public OpenAPIConfig(
            @Value("${openapi.dev_url}") String devUrl,
            @Value("${openapi.prod_url}") String prodUrl
    ) {
        this.devUrl = devUrl;
        this.prodUrl = prodUrl;
    }

    @Bean
    public OpenAPI openAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.description("개발 환경 서버 URL");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.description("운영 환경 서버 URL");

        Info info = new Info()
                .title("Foodbowl API")
                .version("v1.0.0")
                .description("푸드보울 API");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}
