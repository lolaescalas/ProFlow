package com.proflow.proflow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "com.proflow.proflow.repository.mongo"
)
public class MongoConfig {
}