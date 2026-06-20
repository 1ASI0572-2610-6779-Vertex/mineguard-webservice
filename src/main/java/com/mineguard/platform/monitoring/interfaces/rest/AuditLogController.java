package com.mineguard.platform.monitoring.interfaces.rest;

import com.mineguard.platform.monitoring.application.queryservices.AuditLogQueryService;
import com.mineguard.platform.monitoring.domain.model.queries.GetAuditLogQuery;
import com.mineguard.platform.monitoring.interfaces.rest.resources.AuditLogResource;
import com.mineguard.platform.monitoring.interfaces.rest.transform.AuditLogEntryResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/audit-logs", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Audit Logs", description = "Immutable audit trail of all security-relevant and operational events " +
        "in the MineGuard platform. Entries are written automatically when supervisors take actions on alerts, " +
        "when drivers check in or out, and when administrative changes are made. " +
        "The resource name is plural kebab-case (`audit-logs`) following the same convention as all other collections.")
public class AuditLogController {

    private final AuditLogQueryService auditLogQueryService;

    public AuditLogController(AuditLogQueryService auditLogQueryService) {
        this.auditLogQueryService = auditLogQueryService;
    }

    @GetMapping
    @Operation(
            summary = "Get audit log",
            description = "Returns the full audit log for the authenticated company, ordered by occurrence time descending. " +
                    "Each entry records: event type, description, the actor (supervisor or system), " +
                    "and the JSON parameters used to reconstruct the human-readable description. " +
                    "The response is wrapped in an `{ entries: [...] }` envelope to allow future pagination " +
                    "fields to be added without breaking the client contract.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audit log returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<AuditLogResource> getAll() {
        var entries = auditLogQueryService.handle(new GetAuditLogQuery()).stream()
                .map(AuditLogEntryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(new AuditLogResource(entries));
    }
}
