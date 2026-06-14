package com.mineguard.platform.monitoring.interfaces.rest;

import com.mineguard.platform.monitoring.application.commandservices.AlertCommandService;
import com.mineguard.platform.monitoring.application.queryservices.AlertQueryService;
import com.mineguard.platform.monitoring.domain.model.commands.MarkAlertActionCommand;
import com.mineguard.platform.monitoring.domain.model.queries.GetAllAlertsQuery;
import com.mineguard.platform.monitoring.interfaces.rest.resources.AlertActionResource;
import com.mineguard.platform.monitoring.interfaces.rest.resources.MobileAlertResource;
import com.mineguard.platform.monitoring.interfaces.rest.transform.AlertResourceFromEntityAssembler;
import com.mineguard.platform.monitoring.interfaces.rest.transform.MobileAlertResourceFromEntityAssembler;
import com.mineguard.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Mobile alerts endpoint ({@code /alerts}) for the supervisor app. */
@RestController
@RequestMapping(value = "/alerts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Mobile Alerts", description = "Supervisor safety alerts (mobile)")
public class MobileAlertsController {
    private final AlertQueryService alertQueryService;
    private final AlertCommandService alertCommandService;

    public MobileAlertsController(AlertQueryService alertQueryService, AlertCommandService alertCommandService) {
        this.alertQueryService = alertQueryService;
        this.alertCommandService = alertCommandService;
    }

    @GetMapping
    public ResponseEntity<List<MobileAlertResource>> getAll() {
        var alerts = alertQueryService.handle(new GetAllAlertsQuery()).stream()
                .filter(a -> a.getStatus() != com.mineguard.platform.monitoring.domain.model.valueobjects.AlertStatus.RESOLVED)
                .map(MobileAlertResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/{id}/action")
    public ResponseEntity<?> action(@PathVariable Long id, @RequestBody(required = false) AlertActionResource resource) {
        var action = resource != null && resource.action() != null ? resource.action() : "markReviewed";
        var result = alertCommandService.handle(new MarkAlertActionCommand(id, action));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, AlertResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @PostMapping("/{id}/mark-reviewed")
    public ResponseEntity<?> markReviewed(@PathVariable Long id) {
        var result = alertCommandService.handle(new MarkAlertActionCommand(id, "markReviewed"));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, AlertResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}
