package com.prokey.baccaratio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://www.baccaratio.devma.de",
                        "https://www.baccaratio.devma.de",
                        "http://baccaratio.devma.de",
                        "https://baccaratio.devma.de",
                        "http://www.baccaratio.porkolab.hu",
                        "https://www.baccaratio.porkolab.hu",
                        "http://baccaratio.porkolab.hu",
                        "https://baccaratio.porkolab.hu",
                        "http://localhost:4200") // Fejlesztési környezet
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .allowedHeaders("*")
                .maxAge(3600);
    }
}