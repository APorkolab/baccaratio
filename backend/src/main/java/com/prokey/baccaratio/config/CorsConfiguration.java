package com.prokey.baccaratio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Környezeti változó beolvasása
        String corsOriginEnv = System.getenv("CORS_ORIGIN");

        // Alapértelmezett engedélyezett eredetek
        String[] defaultOrigins = {
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

        // Engedélyezett eredetek összegyűjtése, duplikációk kiszűrésével
        Set<String> allowedOriginsSet = new HashSet<>(Arrays.asList(defaultOrigins));

        // Környezeti változó hozzáadása, ha van érték
        if (corsOriginEnv != null && !corsOriginEnv.isEmpty()) {
            allowedOriginsSet.addAll(Arrays.asList(corsOriginEnv.split(",")));
        }

        // Eredeti CORS konfiguráció a duplikációk nélkül
        registry.addMapping("/**")
                .allowedOrigins(allowedOriginsSet.toArray(new String[0])) // Átalakítjuk tömbbé
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .allowedHeaders("*")
                .maxAge(3600);
    }
}