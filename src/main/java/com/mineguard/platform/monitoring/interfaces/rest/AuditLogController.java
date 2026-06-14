package com.mineguard.platform.monitoring.interfaces.rest;

import com.mineguard.platform.monitoring.application.queryservices.AuditLogQueryService;
import com.mineguard.platform.monitoring.domain.model.queries.GetAuditLogQuery;
import com.mineguard.platform.monitoring.interfaces.rest.resources.AuditLogResource;
import com.mineguard.platform.monitoring.interfaces.rest.transform.AuditLogEntryResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Web audit log endpoint ({@code /auditLog}), returns {entries: [...]}. */
@RestController
@RequestMapping(value = "/auditLog", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Audit Log", description = "Security and operational audit log")
public class AuditLogController {
    private final AuditLogQueryService auditLogQueryService;

    public AuditLogController(AuditLogQueryService auditLogQueryService) {
        this.auditLogQueryService = auditLogQueryService;
    }

    @GetMapping
    public ResponseEntity<AuditLogResource> get() {
        var entries = auditLogQueryService.handle(new GetAuditLogQuery()).stream()
                .map(AuditLogEntryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(new AuditLogResource(entries));
    }
}
