package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.AnalyticsHistoryRowQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.AnalyticsHistoryRowResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.AnalyticsHistoryRowResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/analyticsHistoryRows", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics", description = "Analytics and dashboard projections")
public class AnalyticsHistoryRowsController {
    private final AnalyticsHistoryRowQueryService queryService;

    public AnalyticsHistoryRowsController(AnalyticsHistoryRowQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<List<AnalyticsHistoryRowResource>> getAll() {
        var items = queryService.findAll().stream()
                .map(AnalyticsHistoryRowResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
