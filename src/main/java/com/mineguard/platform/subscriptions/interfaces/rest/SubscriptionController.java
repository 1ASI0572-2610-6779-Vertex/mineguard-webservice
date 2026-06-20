package com.mineguard.platform.subscriptions.interfaces.rest;

import com.mineguard.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.mineguard.platform.subscriptions.application.commandservices.CompanyRegistrationCommandService;
import com.mineguard.platform.subscriptions.domain.model.commands.RegisterCompanyCommand;
import com.mineguard.platform.subscriptions.interfaces.rest.resources.CompanyRegistrationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Subscriptions", description = "B2B company self-registration and subscription management. " +
        "Creating a subscription IS the company registration — the two concepts are the same action: " +
        "a mining company signs up for the MineGuard platform and gets a tenant (Company record), " +
        "an administrator account, and an active subscription in a single atomic operation. " +
        "No JWT is required for the creation endpoint (it is the entry point for new customers).")
public class SubscriptionController {

    private final CompanyRegistrationCommandService registrationCommandService;

    public SubscriptionController(CompanyRegistrationCommandService registrationCommandService) {
        this.registrationCommandService = registrationCommandService;
    }

    @PostMapping
    @Operation(
            summary = "Create a subscription (company registration)",
            description = "Registers a new mining company as a MineGuard tenant. This is the landing-page sign-up flow. " +
                    "A single POST to this endpoint atomically: " +
                    "(1) creates the Company record (the tenant); " +
                    "(2) creates an administrator User account with role ADMIN linked to the new company; " +
                    "(3) generates a temporary password and sends it to `adminEmail` via Brevo SMTP; " +
                    "(4) activates the subscription. " +
                    "The administrator can then log in at POST /api/v1/sessions and immediately begin " +
                    "adding supervisors (POST /api/v1/supervisors) and drivers (POST /api/v1/drivers). " +
                    "No JWT is required — this endpoint is public. " +
                    "The former path `/company-registration` was a verb that described the action; " +
                    "the resource being created is the `subscription` itself.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Subscription created — company registered and credentials sent to adminEmail"),
            @ApiResponse(responseCode = "400", description = "Validation error — missing required fields or malformed email"),
            @ApiResponse(responseCode = "409", description = "A company with this email or name already exists")
    })
    public ResponseEntity<?> create(@Valid @RequestBody CompanyRegistrationRequest request) {
        var command = new RegisterCompanyCommand(
                request.companyName(), request.adminFullName(), request.adminEmail());
        var result = registrationCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(result, msg -> msg, HttpStatus.CREATED);
    }
}
