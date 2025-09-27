package com.cyberfoot.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication(scanBasePackages = "com.cyberfoot", exclude = {SecurityAutoConfiguration.class, ReactiveSecurityAutoConfiguration.class})
@EnableR2dbcRepositories(basePackages = {"com.cyberfoot.adapters.persistence.club", "com.cyberfoot.adapters.persistence.fixture"})
public class CyberfootApplication {
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
