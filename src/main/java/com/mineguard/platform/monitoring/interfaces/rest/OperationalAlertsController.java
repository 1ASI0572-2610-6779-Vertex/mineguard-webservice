package com.mineguard.platform.monitoring.interfaces.rest;

import com.mineguard.platform.monitoring.application.commandservices.AlertCommandService;
import com.mineguard.platform.monitoring.application.queryservices.AlertQueryService;
import com.mineguard.platform.monitoring.domain.model.queries.GetAllAlertsQuery;
import com.mineguard.platform.monitoring.interfaces.rest.resources.AlertResource;
import com.mineguard.platform.monitoring.interfaces.rest.resources.UpdateAlertResource;
import com.mineguard.platform.monitoring.interfaces.rest.transform.AlertResourceFromEntityAssembler;
import com.mineguard.platform.monitoring.interfaces.rest.transform.UpdateAlertCommandFromResourceAssembler;
import com.mineguard.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Web operational alerts endpoint ({@code /operationalAlerts}). */
@RestController
@RequestMapping(value = "/operationalAlerts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Operational Alerts", description = "Operational alert management and classification")
public class OperationalAlertsController {
    private final AlertQueryService alertQueryService;
    private final AlertCommandService alertCommandService;

    public OperationalAlertsController(AlertQueryService alertQueryService, AlertCommandService alertCommandService) {
        this.alertQueryService = alertQueryService;
        this.alertCommandService = alertCommandService;
    }

    @GetMapping
    public ResponseEntity<List<AlertResource>> getAll() {
        var alerts = alertQueryService.handle(new GetAllAlertsQuery()).stream()
                .map(AlertResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(alerts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UpdateAlertResource resource) {
        var command = UpdateAlertCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var result = alertCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, AlertResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}
