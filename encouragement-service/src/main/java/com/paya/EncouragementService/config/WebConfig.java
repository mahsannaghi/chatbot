package com.paya.EncouragementService.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableSpringDataWebSupport
public class WebConfig implements WebMvcConfigurer {

//    @Value("${keycloak.login}")
//    private String keycloakServer;
    private static final Dotenv dotenv = Dotenv.load();
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String allowedOrigins = dotenv.get("CORS_ALLOWED_ORIGINS");
        String[] origins = allowedOrigins.split(",");
        registry.addMapping("/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                .allowedHeaders("Authorization", "Content-Type")
                .allowCredentials(true);
    }

//    @Bean
//    public JwtDecoder jwtDecoder() {
//        String jwkSetUri = keycloakServer + "/auth/realms/TebyanWeb/protocol/openid-connect/certs";
//        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
//    }
}
