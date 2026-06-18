package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.AnalyticsFatigueBarQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.AnalyticsFatigueBarResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.AnalyticsFatigueBarResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/analyticsFatigueBars", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics", description = "Analytics and dashboard projections")
public class AnalyticsFatigueBarsController {
    private final AnalyticsFatigueBarQueryService queryService;

    public AnalyticsFatigueBarsController(AnalyticsFatigueBarQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<List<AnalyticsFatigueBarResource>> getAll() {
        var items = queryService.findAll().stream()
                .map(AnalyticsFatigueBarResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
