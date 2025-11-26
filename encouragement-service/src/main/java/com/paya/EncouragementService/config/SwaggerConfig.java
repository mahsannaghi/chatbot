package com.paya.EncouragementService.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT"
)
public class SwaggerConfig {
        @Bean
        public OpenAPI customOpenAPI() {
            return new OpenAPI()
                    .components(new Components())
                    .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                    .info(new Info()
                            .title("Encouragement API")
                            .version("1.0")
                            .description("API documentation for managing Encouragement")
                            .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                    .externalDocs(new ExternalDocumentation()
                            .description("SpringDoc Documentation")
                            .url("https://springdoc.org/"));
        }
    }



