package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.AnalyticsHistoryRowQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.AnalyticsHistoryRowResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.AnalyticsHistoryRowResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics", description = "Deep analytical projections over historical driver, vehicle, and alert data.")
public class AnalyticsHistoryRowsController {

    private final AnalyticsHistoryRowQueryService queryService;

    public AnalyticsHistoryRowsController(AnalyticsHistoryRowQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/history")
    @Operation(
            summary = "Get analytics history",
            description = "Returns the paginated history of analytical records — one row per evaluated shift or event, " +
                    "used to render the data table in the analytics history view. " +
                    "Each row includes: driver, vehicle, trip duration, alert count, fatigue score, and risk classification. " +
                    "The former endpoint name `analyticsHistoryRows` exposed an implementation detail (`Rows`). " +
                    "The canonical resource name is `history`, agnostic of the presentation format.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Analytics history returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<AnalyticsHistoryRowResource>> getHistory() {
        var items = queryService.findAll().stream()
                .map(AnalyticsHistoryRowResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
