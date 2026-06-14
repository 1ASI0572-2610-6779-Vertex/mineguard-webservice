package com.mineguard.platform.monitoring.interfaces.rest;

import com.mineguard.platform.monitoring.application.queryservices.CardiacReadingQueryService;
import com.mineguard.platform.monitoring.domain.model.queries.GetAllCardiacReadingsQuery;
import com.mineguard.platform.monitoring.interfaces.rest.resources.CardiacReadingResource;
import com.mineguard.platform.monitoring.interfaces.rest.transform.CardiacReadingResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Web cardiac readings endpoint ({@code /cardiacReadings}). */
@RestController
@RequestMapping(value = "/cardiacReadings", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Cardiac Readings", description = "Driver cardiac monitoring")
public class CardiacReadingsController {
    private final CardiacReadingQueryService cardiacReadingQueryService;

    public CardiacReadingsController(CardiacReadingQueryService cardiacReadingQueryService) {
        this.cardiacReadingQueryService = cardiacReadingQueryService;
    }

    @GetMapping
    public ResponseEntity<List<CardiacReadingResource>> getAll() {
        var readings = cardiacReadingQueryService.handle(new GetAllCardiacReadingsQuery()).stream()
                .map(CardiacReadingResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(readings);
    }
}
