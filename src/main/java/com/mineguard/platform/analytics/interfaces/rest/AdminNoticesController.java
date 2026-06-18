package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.AdminNoticeQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.AdminNoticesResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.AdminNoticeResourceFromEntityAssembler;
import com.mineguard.platform.monitoring.application.internal.AuditLogWriter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Admin notices endpoint ({@code /adminNotices}), returns {notices: [...]}. */
@RestController
@RequestMapping(value = "/adminNotices", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Admin Notices", description = "Administrative notices")
public class AdminNoticesController {
    private final AdminNoticeQueryService queryService;
    private final AuditLogWriter auditLogWriter;

    public AdminNoticesController(AdminNoticeQueryService queryService, AuditLogWriter auditLogWriter) {
        this.queryService = queryService;
        this.auditLogWriter = auditLogWriter;
    }

    @GetMapping
    public ResponseEntity<AdminNoticesResource> get() {
        var notices = queryService.findAll().stream()
                .map(AdminNoticeResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(new AdminNoticesResource(notices));
    }

    @PostMapping("/{id}/resend")
    public ResponseEntity<Void> resend(@PathVariable Long id) {
        auditLogWriter.record("administrative", "monitoring.audit.entries.noticeResent.title",
                "monitoring.audit.entries.noticeResent.description",
                "{\"noticeId\":" + id + "}", "monitoring.audit.actors.adminGlobal");
        return ResponseEntity.ok().build();
    }
}
