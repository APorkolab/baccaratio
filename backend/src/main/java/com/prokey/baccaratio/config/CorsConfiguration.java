package com.prokey.baccaratio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Környezeti változó beolvasása
        String corsOriginEnv = System.getenv("CORS_ORIGIN");

        // Alapértelmezett engedélyezett eredetek
        String[] allowedOrigins = {
                "http://www.baccaratio.devma.de",
                "https://www.baccaratio.devma.de",
                "http://baccaratio.devma.de",
                "https://baccaratio.devma.de",
                "http://www.baccaratio.porkolab.hu",
                "https://www.baccaratio.porkolab.hu",
                "http://baccaratio.porkolab.hu",
                "https://baccaratio.porkolab.hu",
                "http://localhost:4200"
        };

        // Ha van érvényes érték a CORS_ORIGIN környezeti változóban, hozzáadjuk a
        // listához
        if (corsOriginEnv != null && !corsOriginEnv.isEmpty()) {
            registry.addMapping("/**")
                    .allowedOrigins(concatArrays(allowedOrigins, corsOriginEnv.split(","))) // Környezeti változók
                                                                                            // hozzáadása
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowCredentials(true)
                    .allowedHeaders("*")
                    .maxAge(3600);
        } else {
            registry.addMapping("/**")
                    .allowedOrigins(allowedOrigins)
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowCredentials(true)
                    .allowedHeaders("*")
                    .maxAge(3600);
        }
    }

    // Helper metódus tömbök összefűzésére
    private String[] concatArrays(String[] arr1, String[] arr2) {
        String[] result = new String[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, result, 0, arr1.length);
        System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
        return result;
    }
}