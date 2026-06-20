package com.mineguard.platform.iam.interfaces.rest;

import com.mineguard.platform.assets.application.queryservices.DriverQueryService;
import com.mineguard.platform.assets.domain.model.aggregates.Driver;
import com.mineguard.platform.assets.domain.model.queries.GetDriverByUserIdQuery;
import com.mineguard.platform.iam.application.commandservices.UserCommandService;
import com.mineguard.platform.iam.domain.model.aggregates.User;
import com.mineguard.platform.iam.interfaces.rest.resources.MobileSignInResource;
import com.mineguard.platform.iam.interfaces.rest.transform.MobileAuthenticatedUserResourceFromEntityAssembler;
import com.mineguard.platform.iam.interfaces.rest.transform.MobileSignInCommandFromResourceAssembler;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import com.mineguard.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Sessions (Mobile)", description = "Mobile session creation for field operators (Flutter app). " +
        "A mobile session differs from a web session in three ways: " +
        "(1) credentials use the driver's workerId (format CDT-{companyId}-{seq}) instead of an email; " +
        "(2) the response includes `driverId` (the numeric Driver ID) so the app can immediately call " +
        "POST /api/v1/vehicles/{vehicleId}/trips for check-in without a second round trip; " +
        "(3) non-driver users (supervisors, admins) have `driverId: null` in the response.")
public class MobileAuthenticationController {

    private final UserCommandService userCommandService;
    private final DriverQueryService driverQueryService;

    public MobileAuthenticationController(UserCommandService userCommandService,
                                           DriverQueryService driverQueryService) {
        this.userCommandService = userCommandService;
        this.driverQueryService = driverQueryService;
    }

    @PostMapping("/mobile")
    @Operation(
            summary = "Create a mobile session (operator sign-in)",
            description = "Authenticates a field operator by workerId and password, returning a JWT session for the Flutter app. " +
                    "The `workerId` field corresponds to the operator's login identifier generated at driver creation " +
                    "(format: `CDT-{companyId}-{sequenceNumber}`, e.g. `CDT-1-003`). " +
                    "On success, returns `{ workerId, fullName, role, token, driverId }` where: " +
                    "`token` is the signed JWT to include as `Authorization: Bearer <token>` in all subsequent requests; " +
                    "`driverId` is the numeric ID of the Driver record (used for check-in at " +
                    "POST /api/v1/vehicles/{vehicleId}/trips); " +
                    "`driverId` is `null` for non-driver users such as mobile supervisors. " +
                    "No JWT is required to call this endpoint (it is public). " +
                    "The IAM → Assets cross-context dependency (resolving driverId from userId) is intentional " +
                    "at the controller layer to avoid a second round-trip from the mobile app.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mobile session created — JWT and driverId returned"),
            @ApiResponse(responseCode = "401", description = "Invalid workerId or password"),
            @ApiResponse(responseCode = "403", description = "Account disabled or company subscription inactive")
    })
    public ResponseEntity<?> createMobileSession(@RequestBody MobileSignInResource resource) {
        var command = MobileSignInCommandFromResourceAssembler.toCommandFromResource(resource);
        Result<ImmutablePair<User, String>, ApplicationError> result = userCommandService.handle(command);
        if (result.isFailure()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                    ((Result.Failure<?, ApplicationError>) result).error());
        }
        var pair = result.toOptional().orElseThrow();
        var user = pair.getLeft();
        var token = pair.getRight();
        Long driverId = driverQueryService
                .handle(new GetDriverByUserIdQuery(user.getId()))
                .map(Driver::getId)
                .orElse(null);
        return new ResponseEntity<>(
                MobileAuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(user, token, driverId),
                HttpStatus.OK);
    }
}
