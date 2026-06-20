package com.mineguard.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Bootstrap class for the MineGuard Platform application.
 *
 * <p>Initializes Spring Boot auto-configuration and JPA auditing infrastructure.</p>
 */
@EnableAsync
@EnableJpaAuditing
@SpringBootApplication
public class MineGuardPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(MineGuardPlatformApplication.class, args);
    }

}
