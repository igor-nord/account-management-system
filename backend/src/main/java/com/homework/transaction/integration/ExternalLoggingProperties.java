package com.homework.transaction.integration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "external-logging")
public record ExternalLoggingProperties(
        String baseUrl,
        int status,
        Duration connectTimeout,
        Duration readTimeout) {
}
