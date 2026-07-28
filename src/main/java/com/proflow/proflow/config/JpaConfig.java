package com.proflow.proflow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.proflow.proflow.repository.postgres"
)
public class JpaConfig {
}