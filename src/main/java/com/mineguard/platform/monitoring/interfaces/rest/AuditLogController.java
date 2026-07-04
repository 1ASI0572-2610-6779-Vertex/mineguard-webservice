package com.mineguard.platform.monitoring.interfaces.rest;

import com.mineguard.platform.monitoring.application.queryservices.AuditLogQueryService;
import com.mineguard.platform.monitoring.domain.model.aggregates.AuditLogEntry;
import com.mineguard.platform.monitoring.domain.model.queries.GetAuditLogQuery;
import com.mineguard.platform.monitoring.interfaces.rest.resources.AuditLogResource;
import com.mineguard.platform.monitoring.interfaces.rest.transform.AuditLogEntryResourceFromEntityAssembler;
import com.mineguard.platform.shared.infrastructure.export.ExcelExportUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/audit-logs", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Audit Logs", description = "Immutable audit trail of all security-relevant and operational events " +
        "in the MineGuard platform. Entries are written automatically when supervisors take actions on alerts, " +
        "when drivers check in or out, and when administrative changes are made. " +
        "The resource name is plural kebab-case (`audit-logs`) following the same convention as all other collections. " +
        "Downloadable as PDF or Excel via the `format` query parameter for company-wide audits.")
public class AuditLogController {

    private final AuditLogQueryService auditLogQueryService;

    public AuditLogController(AuditLogQueryService auditLogQueryService) {
        this.auditLogQueryService = auditLogQueryService;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PDF_VALUE})
    @Operation(
            summary = "Get audit log — JSON, PDF, or Excel",
            description = "Returns the full audit log for the authenticated company, ordered by occurrence time descending. " +
                    "Each entry records: event type, description, the actor (supervisor or system), " +
                    "and the JSON parameters used to reconstruct the human-readable description. " +
                    "The representation is selected via the `format` query parameter:\n\n" +
                    "- **Omit `format`** (default): returns JSON wrapped in an `{ entries: [...] }` envelope.\n\n" +
                    "- **`format=pdf`**: returns the audit trail as a PDF binary file (attachment download), " +
                    "so a company administrator can archive or hand over a full audit for compliance review.\n\n" +
                    "- **`format=xls`**: returns the audit trail as an Excel (.xlsx) binary file, one row per entry, " +
                    "for offline filtering/pivoting.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audit log returned as JSON (default), PDF, or Excel"),
            @ApiResponse(responseCode = "400", description = "Unsupported format value (only `pdf`/`xls` are accepted besides the default)"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> getAll(
            @Parameter(description = "Optional representation format. Pass `pdf` or `xls` to receive a downloadable file.")
            @RequestParam(required = false) String format) {
        var entries = auditLogQueryService.handle(new GetAuditLogQuery());

        if ("pdf".equalsIgnoreCase(format)) {
            var content = buildPdfContent(entries);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mineguard-audit-log.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(content.getBytes(StandardCharsets.UTF_8));
        }
        if ("xls".equalsIgnoreCase(format)) {
            var bytes = ExcelExportUtil.toXlsx("Audit Log", AUDIT_LOG_HEADERS, toRows(entries));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mineguard-audit-log.xlsx")
                    .contentType(MediaType.parseMediaType(ExcelExportUtil.XLSX_CONTENT_TYPE))
                    .body(bytes);
        }

        var resources = entries.stream()
                .map(AuditLogEntryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(new AuditLogResource(resources));
    }

    private static final List<String> AUDIT_LOG_HEADERS =
            List.of("ID", "Category", "Occurred At", "Title Key", "Description Key", "Actor Key", "Description Params");

    private List<List<String>> toRows(List<AuditLogEntry> entries) {
        return entries.stream()
                .map(e -> List.of(
                        String.valueOf(e.getId()),
                        nullToEmpty(e.getCategory()),
                        nullToEmpty(e.getOccurredAt()),
                        nullToEmpty(e.getTitleKey()),
                        nullToEmpty(e.getDescriptionKey()),
                        nullToEmpty(e.getActorKey()),
                        nullToEmpty(e.getDescriptionParamsJson())))
                .toList();
    }

    private String buildPdfContent(List<AuditLogEntry> entries) {
        var builder = new StringBuilder("MineGuard Audit Log\n\n");
        for (var e : entries) {
            builder.append("[").append(e.getOccurredAt()).append("] ")
                    .append(e.getCategory()).append(" — ")
                    .append(e.getTitleKey()).append(" / ").append(e.getDescriptionKey())
                    .append(" (actor: ").append(e.getActorKey()).append(")\n");
        }
        return builder.toString();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
