package com.mineguard.platform.subscriptions.interfaces.rest;
import com.mineguard.platform.subscriptions.application.queryservices.SubscriptionQueryService;
import com.mineguard.platform.subscriptions.domain.model.queries.GetPlansQuery;
import com.mineguard.platform.subscriptions.interfaces.rest.resources.PlanResource;
import com.mineguard.platform.subscriptions.interfaces.rest.transform.PlanResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping(value = "/api/v1/plans", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Plans", description = "Available subscription plans")
public class PlansController {
    private final SubscriptionQueryService subscriptionQueryService;
    public PlansController(SubscriptionQueryService subscriptionQueryService) {
        this.subscriptionQueryService = subscriptionQueryService;
    }
    @GetMapping
    @Operation(summary = "List plans", description = "Returns all available subscription plans.")
    public ResponseEntity<List<PlanResource>> getPlans() {
        var plans = subscriptionQueryService.handle(new GetPlansQuery()).stream()
                .map(PlanResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(plans);
    }
}
