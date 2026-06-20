package com.mineguard.platform.subscriptions.interfaces.rest;
import com.mineguard.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.mineguard.platform.subscriptions.application.commandservices.SubscriptionCommandService;
import com.mineguard.platform.subscriptions.domain.model.commands.ProcessPaymentCommand;
import com.mineguard.platform.subscriptions.domain.model.commands.RetryPaymentCommand;
import com.mineguard.platform.subscriptions.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping(value = "/api/v1/subscriptions/{subscriptionId}/payments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Payments", description = "Payment operations for subscriptions")
public class PaymentsController {
    private final SubscriptionCommandService subscriptionCommandService;
    public PaymentsController(SubscriptionCommandService subscriptionCommandService) {
        this.subscriptionCommandService = subscriptionCommandService;
    }
    @PostMapping
    @Operation(summary = "Process payment")
    public ResponseEntity<?> processPayment(@PathVariable Long subscriptionId) {
        var result = subscriptionCommandService.handle(new ProcessPaymentCommand(subscriptionId));
        return ResponseEntityAssembler.toResponseEntityFromResult(result,
                SubscriptionResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
    @PostMapping("/{paymentId}/retry")
    @Operation(summary = "Retry payment")
    public ResponseEntity<?> retryPayment(@PathVariable Long subscriptionId, @PathVariable Long paymentId) {
        var result = subscriptionCommandService.handle(new RetryPaymentCommand(subscriptionId, paymentId));
        return ResponseEntityAssembler.toResponseEntityFromResult(result,
                SubscriptionResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}
