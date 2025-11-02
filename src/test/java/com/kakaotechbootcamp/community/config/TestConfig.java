package com.kakaotechbootcamp.community.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Instant;
import java.util.Optional;

@TestConfiguration
@EnableJpaAuditing(dateTimeProviderRef = "testDateTimeProvider")
public class TestConfig {

    @Bean
    public DateTimeProvider testDateTimeProvider() {
        return () -> Optional.of(Instant.now());
    }
}
