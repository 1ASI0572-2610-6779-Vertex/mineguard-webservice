package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.AdminNoticeQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.AdminNoticesResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.AdminNoticeResourceFromEntityAssembler;
import com.mineguard.platform.monitoring.application.internal.AuditLogWriter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/admin", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Admin", description = "Administrative panel resources.")
public class AdminNoticesController {

    private final AdminNoticeQueryService queryService;
    private final AuditLogWriter auditLogWriter;

    public AdminNoticesController(AdminNoticeQueryService queryService, AuditLogWriter auditLogWriter) {
        this.queryService = queryService;
        this.auditLogWriter = auditLogWriter;
    }

    @GetMapping("/notices")
    @Operation(
            summary = "List admin notices",
            description = "Returns all administrative notices visible to the platform admin panel, " +
                    "wrapped in a `{ notices: [...] }` envelope. " +
                    "Notices are system-generated messages about subscription changes, billing alerts, " +
                    "or company registration events. " +
                    "To re-send a notice, POST to /api/v1/admin/notices/{noticeId}/dispatches.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notices returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — ADMIN role required")
    })
    public ResponseEntity<AdminNoticesResource> getNotices() {
        var notices = queryService.findAll().stream()
                .map(AdminNoticeResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(new AdminNoticesResource(notices));
    }

    @PostMapping("/notices/{noticeId}/dispatches")
    @Operation(
            summary = "Dispatch (re-send) a notice",
            description = "Creates a new dispatch record for the specified notice, triggering a re-send to the " +
                    "notice's original recipient(s). " +
                    "The action is modeled as creating a `dispatch` sub-resource (noun) rather than calling a " +
                    "`resend` verb — the dispatch IS the re-sending event, and it is persisted in the audit log. " +
                    "Returns 200 with an empty body on success; the dispatch is recorded as an audit log entry " +
                    "with event type `administrative` and notice ID in the description parameters.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dispatch created — notice re-sent and audit entry recorded"),
            @ApiResponse(responseCode = "404", description = "Notice not found"),
            @ApiResponse(responseCode = "403", description = "Access denied — ADMIN role required")
    })
    public ResponseEntity<Void> createDispatch(
            @Parameter(description = "Unique numeric identifier of the notice to re-send", required = true)
            @PathVariable("noticeId") Long noticeId) {
        auditLogWriter.record("administrative", "monitoring.audit.entries.noticeResent.title",
                "monitoring.audit.entries.noticeResent.description",
                "{\"noticeId\":" + noticeId + "}", "monitoring.audit.actors.adminGlobal");
        return ResponseEntity.ok().build();
    }
}
