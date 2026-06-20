package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.AnalyticsInsightQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.AnalyticsInsightResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.AnalyticsInsightResourceFromEntityAssembler;
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
public class AnalyticsInsightsController {

    private final AnalyticsInsightQueryService queryService;

    public AnalyticsInsightsController(AnalyticsInsightQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/insights")
    @Operation(
            summary = "Get analytics insights",
            description = "Returns a list of pre-computed natural-language insights derived from the company's " +
                    "historical data. Each insight is a machine-generated observation such as: " +
                    "'Driver CDT-1-003 has 3× more fatigue alerts than average this week' or " +
                    "'Proximity incidents increased 40% on night shifts'. " +
                    "Insights are computed periodically by the analytics projection engine and cached; " +
                    "they are not real-time but are refreshed after each batch analysis run.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Insights returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<AnalyticsInsightResource>> getInsights() {
        var items = queryService.findAll().stream()
                .map(AnalyticsInsightResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
