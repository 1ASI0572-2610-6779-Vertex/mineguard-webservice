package com.mineguard.platform.monitoring.interfaces.rest;

import com.mineguard.platform.monitoring.application.commandservices.AlertCommandService;
import com.mineguard.platform.monitoring.application.queryservices.AlertQueryService;
import com.mineguard.platform.monitoring.domain.model.commands.MarkAlertActionCommand;
import com.mineguard.platform.monitoring.domain.model.queries.GetAlertByIdQuery;
import com.mineguard.platform.monitoring.domain.model.queries.GetAllAlertsQuery;
import com.mineguard.platform.monitoring.domain.repositories.AuditLogEntryRepository;
import com.mineguard.platform.monitoring.interfaces.rest.resources.AlertActionResource;
import com.mineguard.platform.monitoring.interfaces.rest.resources.AlertHistoryResource;
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
    private final AuditLogEntryRepository auditLogEntryRepository;

    public MobileAlertsController(AlertQueryService alertQueryService, AlertCommandService alertCommandService,
                                  AuditLogEntryRepository auditLogEntryRepository) {
        this.alertQueryService = alertQueryService;
        this.alertCommandService = alertCommandService;
        this.auditLogEntryRepository = auditLogEntryRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        var alert = alertQueryService.handle(new GetAlertByIdQuery(id));
        if (alert.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(AlertResourceFromEntityAssembler.toResourceFromEntity(alert.get()));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<AlertHistoryResource>> history(@PathVariable Long id) {
        var token = "\"alertId\":" + id;
        var history = auditLogEntryRepository.findAll().stream()
                .filter(e -> e.getDescriptionParamsJson() != null && e.getDescriptionParamsJson().contains(token))
                .map(e -> new AlertHistoryResource(extract(e.getDescriptionParamsJson(), "action"),
                        extract(e.getDescriptionParamsJson(), "performedBy"), e.getOccurredAt()))
                .toList();
        return ResponseEntity.ok(history);
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

    private String extract(String json, String field) {
        var marker = "\"" + field + "\":\"";
        var start = json.indexOf(marker);
        if (start < 0) return "";
        start += marker.length();
        var end = json.indexOf('"', start);
        return end < 0 ? "" : json.substring(start, end);
    }
}
