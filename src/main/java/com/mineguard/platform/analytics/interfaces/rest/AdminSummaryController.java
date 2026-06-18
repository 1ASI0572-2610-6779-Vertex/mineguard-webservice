package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.AdminSummaryQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.AdminSummaryResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.AdminSummaryResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/adminSummary", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics", description = "Analytics and dashboard projections")
public class AdminSummaryController {
    private final AdminSummaryQueryService queryService;

    public AdminSummaryController(AdminSummaryQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<List<AdminSummaryResource>> getAll() {
        var items = queryService.findAll().stream()
                .map(AdminSummaryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
