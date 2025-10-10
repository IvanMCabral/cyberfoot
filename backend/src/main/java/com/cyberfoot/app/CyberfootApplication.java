package com.cyberfoot.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication(scanBasePackages = "com.cyberfoot", exclude = {SecurityAutoConfiguration.class, ReactiveSecurityAutoConfiguration.class})
@EnableReactiveMongoRepositories(basePackages = {
    "com.cyberfoot.adapters.persistence.club",
    "com.cyberfoot.adapters.persistence.season",
    "com.cyberfoot.adapters.persistence.fixture",
    "com.cyberfoot.adapters.persistence.player",
    "com.cyberfoot.adapters.repository",
    "com.cyberfoot.adapters.persistence.gamesession"
})
public class CyberfootApplication {
    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(@Value("${spring.data.mongodb.uri}") String mongoUri) {
        return new ReactiveMongoTemplate(MongoClients.create(mongoUri), "cyberfoot");
    }
    private static final Logger logger = LogManager.getLogger(CyberfootApplication.class);
    public static void main(String[] args) throws Exception {
        // Ubicación final: backend/logs (si corrés desde backend/app)
        // Creamos el directorio si no existe
        Path logDir = Paths.get(System.getProperty("user.dir"), "logs").normalize();
        Files.createDirectories(logDir);
        SpringApplication.run(CyberfootApplication.class, args);
        logger.info("Cyberfoot backend started. Working directory: {}", System.getProperty("user.dir"));
        logger.info("Log directory: {}", logDir);
    }

}
// TODO[M1]: Agregar CommandLineRunner para seed inicial
