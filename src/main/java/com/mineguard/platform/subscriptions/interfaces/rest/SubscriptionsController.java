package com.mineguard.platform.subscriptions.interfaces.rest;
import com.mineguard.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.mineguard.platform.subscriptions.application.commandservices.SubscriptionCommandService;
import com.mineguard.platform.subscriptions.application.queryservices.SubscriptionQueryService;
import com.mineguard.platform.subscriptions.domain.model.commands.*;
import com.mineguard.platform.subscriptions.domain.model.queries.GetSubscriptionByIdQuery;
import com.mineguard.platform.subscriptions.domain.model.queries.GetSubscriptionsByUserQuery;
import com.mineguard.platform.subscriptions.interfaces.rest.resources.CreateSubscriptionResource;
import com.mineguard.platform.subscriptions.interfaces.rest.resources.SubscriptionResource;
import com.mineguard.platform.subscriptions.interfaces.rest.transform.CreateSubscriptionCommandFromResourceAssembler;
import com.mineguard.platform.subscriptions.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping(value = "/api/v1/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Subscriptions", description = "Subscription lifecycle management")
public class SubscriptionsController {
    private final SubscriptionCommandService subscriptionCommandService;
    private final SubscriptionQueryService subscriptionQueryService;
    public SubscriptionsController(SubscriptionCommandService subscriptionCommandService,
                                   SubscriptionQueryService subscriptionQueryService) {
        this.subscriptionCommandService = subscriptionCommandService;
        this.subscriptionQueryService = subscriptionQueryService;
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get subscription by id")
    public ResponseEntity<?> getSubscriptionById(@PathVariable Long id) {
        return subscriptionQueryService.handle(new GetSubscriptionByIdQuery(id))
                .map(subscription -> ResponseEntity.ok(SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get subscriptions by user")
    public ResponseEntity<List<SubscriptionResource>> getSubscriptionsByUser(@PathVariable Long userId) {
        var subscriptions = subscriptionQueryService.handle(new GetSubscriptionsByUserQuery(userId)).stream()
                .map(SubscriptionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(subscriptions);
    }
    @PostMapping
    @Operation(summary = "Create subscription")
    public ResponseEntity<?> createSubscription(@RequestBody CreateSubscriptionResource resource) {
        var command = CreateSubscriptionCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = subscriptionCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(result,
                SubscriptionResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }
    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel subscription")
    public ResponseEntity<?> cancelSubscription(@PathVariable Long id) {
        var result = subscriptionCommandService.handle(new CancelSubscriptionCommand(id));
        return ResponseEntityAssembler.toResponseEntityFromResult(result,
                SubscriptionResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
    @PostMapping("/{id}/renew")
    @Operation(summary = "Renew subscription")
    public ResponseEntity<?> renewSubscription(@PathVariable Long id) {
        var result = subscriptionCommandService.handle(new RenewSubscriptionCommand(id));
        return ResponseEntityAssembler.toResponseEntityFromResult(result,
                SubscriptionResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
    @PutMapping("/{id}/payment-method")
    @Operation(summary = "Update payment method")
    public ResponseEntity<?> updatePaymentMethod(@PathVariable Long id, @RequestBody CreateSubscriptionResource resource) {
        var result = subscriptionCommandService.handle(new UpdatePaymentMethodCommand(id,
                resource.paymentMethodType(), resource.paymentMethodDetails()));
        return ResponseEntityAssembler.toResponseEntityFromResult(result,
                SubscriptionResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}
