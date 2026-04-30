package com.projetogs.mylibrary.config;

import java.time.Duration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


@Testcontainers

public abstract class MongoTestConfig {
    @Container
    static final MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.29"))
                    .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);

        // opcionais, mas “blindam” o contexto caso algo leia isso no startup:
        registry.add("spring.data.mongodb.database", () -> "mylibrary_test");
        registry.add("api.security.token.secret", () -> "test-secret-ci-mais-de-32-caracteres");
    }
}
