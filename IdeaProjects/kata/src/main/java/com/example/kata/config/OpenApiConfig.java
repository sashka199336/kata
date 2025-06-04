package com.example.kata.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("public-apis")
                .packagesToScan("com.example.kata") // исправьте пакет при необходимости
                .build();
    }

    // 🚦 Бин для логгирования HTTP-запросов
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true); // Логировать тело запроса
        loggingFilter.setMaxPayloadLength(10000); // Максимальная длина логируемого тела
        return loggingFilter;
    }
}