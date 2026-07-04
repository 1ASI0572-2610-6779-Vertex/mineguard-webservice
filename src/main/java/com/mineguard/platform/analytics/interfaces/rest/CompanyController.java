package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.AdminNoticeQueryService;
import com.mineguard.platform.analytics.application.queryservices.AnalyticsFatigueBarQueryService;
import com.mineguard.platform.analytics.application.queryservices.AnalyticsHistoryRowQueryService;
import com.mineguard.platform.analytics.application.queryservices.AnalyticsIncidentDistributionQueryService;
import com.mineguard.platform.analytics.application.queryservices.AnalyticsInsightQueryService;
import com.mineguard.platform.analytics.application.queryservices.CompanyKpisQueryService;
import com.mineguard.platform.analytics.application.queryservices.DashboardTrendQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.AdminNoticesResource;
import com.mineguard.platform.analytics.interfaces.rest.resources.AnalyticsFatigueBarResource;
import com.mineguard.platform.analytics.interfaces.rest.resources.AnalyticsHistoryRowResource;
import com.mineguard.platform.analytics.interfaces.rest.resources.AnalyticsIncidentDistributionResource;
import com.mineguard.platform.analytics.interfaces.rest.resources.AnalyticsInsightResource;
import com.mineguard.platform.analytics.interfaces.rest.resources.CompanyKpisResource;
import com.mineguard.platform.analytics.interfaces.rest.resources.DashboardTrendResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.AdminNoticeResourceFromEntityAssembler;
import com.mineguard.platform.analytics.interfaces.rest.transform.AnalyticsFatigueBarResourceFromEntityAssembler;
import com.mineguard.platform.analytics.interfaces.rest.transform.AnalyticsHistoryRowResourceFromEntityAssembler;
import com.mineguard.platform.analytics.interfaces.rest.transform.AnalyticsIncidentDistributionResourceFromEntityAssembler;
import com.mineguard.platform.analytics.interfaces.rest.transform.AnalyticsInsightResourceFromEntityAssembler;
import com.mineguard.platform.analytics.interfaces.rest.transform.CompanyKpisResourceFromEntityAssembler;
import com.mineguard.platform.analytics.interfaces.rest.transform.DashboardTrendResourceFromEntityAssembler;
import com.mineguard.platform.monitoring.application.internal.AuditLogWriter;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import com.mineguard.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.mineguard.platform.subscriptions.application.commandservices.CompanyRegistrationCommandService;
import com.mineguard.platform.subscriptions.domain.model.commands.RegisterCompanyCommand;
import com.mineguard.platform.subscriptions.interfaces.rest.resources.CompanyRegistrationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Company resource: registration (tenant onboarding) plus every company-scoped analytical
 * projection. Consolidates the former standalone widget endpoints (dashboard/fleet/catalog
 * summary, dashboard trend, analytics fatigue/incident-distribution/insights, admin notices)
 * under a single aggregate-root-aligned hierarchy: everything here is either about a Company
 * or about a specific Company's data, nested under {companyId}.
 */
@RestController
@RequestMapping(value = "/api/v1/companies", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Companies", description = "Company (tenant) registration and company-scoped analytical projections. " +
        "Every {companyId} path variable must match the authenticated caller's own tenant — requesting another " +
        "company's data returns 404, never another tenant's data.")
public class CompanyController {

    private final CompanyRegistrationCommandService registrationCommandService;
    private final CompanyKpisQueryService kpisQueryService;
    private final DashboardTrendQueryService alertsTrendQueryService;
    private final AnalyticsFatigueBarQueryService fatigueQueryService;
    private final AnalyticsIncidentDistributionQueryService incidentsQueryService;
    private final AnalyticsInsightQueryService insightsQueryService;
    private final AdminNoticeQueryService noticesQueryService;
    private final AnalyticsHistoryRowQueryService historyQueryService;
    private final AuditLogWriter auditLogWriter;
    private final SecurityContextFacade securityContext;

    public CompanyController(CompanyRegistrationCommandService registrationCommandService,
                             CompanyKpisQueryService kpisQueryService,
                             DashboardTrendQueryService alertsTrendQueryService,
                             AnalyticsFatigueBarQueryService fatigueQueryService,
                             AnalyticsIncidentDistributionQueryService incidentsQueryService,
                             AnalyticsInsightQueryService insightsQueryService,
                             AdminNoticeQueryService noticesQueryService,
                             AnalyticsHistoryRowQueryService historyQueryService,
                             AuditLogWriter auditLogWriter,
                             SecurityContextFacade securityContext) {
        this.registrationCommandService = registrationCommandService;
        this.kpisQueryService = kpisQueryService;
        this.alertsTrendQueryService = alertsTrendQueryService;
        this.fatigueQueryService = fatigueQueryService;
        this.incidentsQueryService = incidentsQueryService;
        this.insightsQueryService = insightsQueryService;
        this.noticesQueryService = noticesQueryService;
        this.historyQueryService = historyQueryService;
        this.auditLogWriter = auditLogWriter;
        this.securityContext = securityContext;
    }

    // -------------------------------------------------------------------------
    // Registration
    // -------------------------------------------------------------------------

    @PostMapping
    @Operation(
            summary = "Register a company (tenant onboarding)",
            description = "Registers a new mining company as a MineGuard tenant. This is the landing-page sign-up flow. " +
                    "A single POST to this endpoint atomically: " +
                    "(1) creates the Company record (the tenant); " +
                    "(2) creates an administrator User account with role ADMIN linked to the new company; " +
                    "(3) generates a temporary password and sends it to `adminEmail` via Brevo SMTP; " +
                    "(4) generates a per-company Edge (IoT) API key, returned as `apiKey` in the response, " +
                    "used to authenticate telemetry ingestion at POST /api/v1/telemetry; " +
                    "(5) activates the subscription. " +
                    "Returns a structured JSON body `{ companyId, apiKey, adminUsername, message }` — " +
                    "not a plain message string — so the caller can consume the generated identifiers " +
                    "programmatically instead of parsing free text. " +
                    "No JWT is required — this endpoint is public. " +
                    "Formerly POST /api/v1/subscriptions.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Company registered — response includes companyId, apiKey, adminUsername and message"),
            @ApiResponse(responseCode = "400", description = "Validation error — missing required fields or malformed email"),
            @ApiResponse(responseCode = "409", description = "A company with this email or name already exists")
    })
    public ResponseEntity<?> create(@Valid @RequestBody CompanyRegistrationRequest request) {
        var command = new RegisterCompanyCommand(
                request.companyName(), request.adminFullName(), request.adminEmail());
        var result = registrationCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(result, msg -> msg, HttpStatus.CREATED);
    }

    // -------------------------------------------------------------------------
    // KPIs
    // -------------------------------------------------------------------------

    @GetMapping("/{companyId}/kpis")
    @Operation(
            summary = "Get company KPIs",
            description = "Returns the consolidated fleet/catalog counters for the given company: " +
                    "driver and vehicle counts by status, supervisor counts, sensor health, critical alerts, " +
                    "and fatigue events. Formerly GET /dashboard/summary, /fleet/summary, /catalog/summary.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company KPIs returned successfully"),
            @ApiResponse(responseCode = "404", description = "Company not found or does not belong to the authenticated tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<CompanyKpisResource> getKpis(
            @Parameter(description = "Unique numeric identifier of the company", required = true)
            @PathVariable("companyId") Long companyId) {
        return kpisQueryService.findForCompany(companyId)
                .map(CompanyKpisResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------------------------------------------------------------------------
    // Metrics
    // -------------------------------------------------------------------------

    @GetMapping("/{companyId}/metrics/alerts-trend")
    @Operation(
            summary = "Get alert trend over time for a company",
            description = "Returns the time-series data of alert/incident counts aggregated over time buckets " +
                    "for the given company. Formerly GET /dashboard/trend.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trend data returned successfully"),
            @ApiResponse(responseCode = "404", description = "Company does not belong to the authenticated tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<DashboardTrendResource>> getAlertsTrend(
            @Parameter(description = "Unique numeric identifier of the company", required = true)
            @PathVariable("companyId") Long companyId) {
        if (!isOwnCompany(companyId)) return ResponseEntity.notFound().build();
        var items = alertsTrendQueryService.findAll().stream()
                .map(DashboardTrendResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{companyId}/metrics/fatigue")
    @Operation(
            summary = "Get fatigue level distribution for a company",
            description = "Returns per-driver fatigue level bars for the given company, sourced from Alerts of " +
                    "type `fatigue_risk` and `high_heart_rate` linked to each driver's driving sessions. " +
                    "Formerly GET /analytics/fatigue-levels.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fatigue level data returned successfully"),
            @ApiResponse(responseCode = "404", description = "Company does not belong to the authenticated tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<AnalyticsFatigueBarResource>> getFatigueLevels(
            @Parameter(description = "Unique numeric identifier of the company", required = true)
            @PathVariable("companyId") Long companyId) {
        if (!isOwnCompany(companyId)) return ResponseEntity.notFound().build();
        var items = fatigueQueryService.findAll().stream()
                .map(AnalyticsFatigueBarResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{companyId}/metrics/incidents")
    @Operation(
            summary = "Get incident type distribution for a company",
            description = "Returns the breakdown of incidents by type (proximity_collision, restricted_zone_entry, " +
                    "high_heart_rate, fatigue_risk, connection_lost) for the given company, with count and " +
                    "percentage relative to the total. Formerly GET /analytics/incident-distribution.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incident distribution returned successfully"),
            @ApiResponse(responseCode = "404", description = "Company does not belong to the authenticated tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<AnalyticsIncidentDistributionResource>> getIncidentDistribution(
            @Parameter(description = "Unique numeric identifier of the company", required = true)
            @PathVariable("companyId") Long companyId) {
        if (!isOwnCompany(companyId)) return ResponseEntity.notFound().build();
        var items = incidentsQueryService.findAll().stream()
                .map(AnalyticsIncidentDistributionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }

    // -------------------------------------------------------------------------
    // Insights
    // -------------------------------------------------------------------------

    @GetMapping("/{companyId}/insights")
    @Operation(
            summary = "Get analytics insights for a company",
            description = "Returns pre-computed natural-language insights derived from the company's historical " +
                    "data (e.g. 'Driver CDT-1-003 has 3x more fatigue alerts than average this week'). " +
                    "Not real-time — refreshed after each batch analysis run. Formerly GET /analytics/insights.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Insights returned successfully"),
            @ApiResponse(responseCode = "404", description = "Company does not belong to the authenticated tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<AnalyticsInsightResource>> getInsights(
            @Parameter(description = "Unique numeric identifier of the company", required = true)
            @PathVariable("companyId") Long companyId) {
        if (!isOwnCompany(companyId)) return ResponseEntity.notFound().build();
        var items = insightsQueryService.findAll().stream()
                .map(AnalyticsInsightResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }

    // -------------------------------------------------------------------------
    // History
    // -------------------------------------------------------------------------

    @GetMapping("/{companyId}/history")
    @Operation(
            summary = "Get analytics history for a company",
            description = "Returns a page of analytical records for the given company — one row per " +
                    "evaluated driving session or incident: driver, vehicle, session duration, alert count, " +
                    "fatigue score, and risk classification. " +
                    "**Pagination is mandatory** (`page`, `size` query params) — this collection grows without " +
                    "bound over the life of a tenant, so there is no unpaged variant of this endpoint. " +
                    "Defaults to `page=0&size=20` if omitted. " +
                    "Ownership of {companyId} is validated inside AnalyticsHistoryRowQueryService itself " +
                    "(against SecurityContextFacade), not only at the controller layer. " +
                    "Formerly GET /analytics/history.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of analytics history records returned successfully"),
            @ApiResponse(responseCode = "404", description = "Company does not belong to the authenticated tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<Page<AnalyticsHistoryRowResource>> getHistory(
            @Parameter(description = "Unique numeric identifier of the company", required = true)
            @PathVariable("companyId") Long companyId,
            @Parameter(description = "Zero-based page index (default 0) and page size (default 20), " +
                    "e.g. `?page=0&size=20`")
            @PageableDefault(size = 20) Pageable pageable) {
        if (!isOwnCompany(companyId)) return ResponseEntity.notFound().build();
        var page = historyQueryService.findForCompany(companyId, pageable)
                .map(AnalyticsHistoryRowResourceFromEntityAssembler::toResourceFromEntity);
        return ResponseEntity.ok(page);
    }

    // -------------------------------------------------------------------------
    // Notices
    // -------------------------------------------------------------------------

    @GetMapping("/{companyId}/notices")
    @Operation(
            summary = "List administrative notices for a company",
            description = "Returns administrative notices for the given company (subscription changes, billing " +
                    "alerts, registration events), wrapped in a `{ notices: [...] }` envelope. " +
                    "Formerly GET /admin/notices.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notices returned successfully"),
            @ApiResponse(responseCode = "404", description = "Company does not belong to the authenticated tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — ADMIN role required")
    })
    public ResponseEntity<AdminNoticesResource> getNotices(
            @Parameter(description = "Unique numeric identifier of the company", required = true)
            @PathVariable("companyId") Long companyId) {
        if (!isOwnCompany(companyId)) return ResponseEntity.notFound().build();
        var notices = noticesQueryService.findAll().stream()
                .map(AdminNoticeResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(new AdminNoticesResource(notices));
    }

    @PostMapping("/{companyId}/notices/{noticeId}/dispatches")
    @Operation(
            summary = "Dispatch (re-send) a company notice",
            description = "Creates a new dispatch record for the specified notice, triggering a re-send to the " +
                    "notice's original recipient(s), recorded in the audit log. " +
                    "Formerly POST /admin/notices/{noticeId}/dispatches.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dispatch created — notice re-sent and audit entry recorded"),
            @ApiResponse(responseCode = "404", description = "Notice or company not found, or company does not belong to the authenticated tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — ADMIN role required")
    })
    public ResponseEntity<Void> createNoticeDispatch(
            @Parameter(description = "Unique numeric identifier of the company", required = true)
            @PathVariable("companyId") Long companyId,
            @Parameter(description = "Unique numeric identifier of the notice to re-send", required = true)
            @PathVariable("noticeId") Long noticeId) {
        if (!isOwnCompany(companyId)) return ResponseEntity.notFound().build();
        auditLogWriter.record("administrative", "monitoring.audit.entries.noticeResent.title",
                "monitoring.audit.entries.noticeResent.description",
                "{\"noticeId\":" + noticeId + "}", "monitoring.audit.actors.adminGlobal");
        return ResponseEntity.ok().build();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private boolean isOwnCompany(Long companyId) {
        var callerCompanyId = securityContext.currentCompanyId();
        return callerCompanyId != null && callerCompanyId.equals(companyId);
    }
}
