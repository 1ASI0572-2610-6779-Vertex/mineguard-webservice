package com.mineguard.platform.iam.application.internal.eventhandlers;

import com.mineguard.platform.iam.application.commandservices.RoleCommandService;
import com.mineguard.platform.iam.domain.model.commands.SeedRolesCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/** Seeds the default roles once the application context is ready. */
@Service
public class ApplicationReadyEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationReadyEventHandler.class);
    private final RoleCommandService roleCommandService;

    public ApplicationReadyEventHandler(RoleCommandService roleCommandService) {
        this.roleCommandService = roleCommandService;
    }

    @Order(1)
    @EventListener(ApplicationReadyEvent.class)
    public void on(ApplicationReadyEvent event) {
        var applicationName = event.getApplicationContext().getId();
        LOGGER.info("Starting roles seeding for {}", applicationName);
        roleCommandService.handle(new SeedRolesCommand());
        LOGGER.info("Roles seeding finished for {}", applicationName);
    }
}
