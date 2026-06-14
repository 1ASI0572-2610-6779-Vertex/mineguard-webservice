package com.mineguard.platform.iam.interfaces.rest;

import com.mineguard.platform.iam.application.commandservices.UserCommandService;
import com.mineguard.platform.iam.interfaces.rest.resources.MobileSignInResource;
import com.mineguard.platform.iam.interfaces.rest.transform.MobileAuthenticatedUserResourceFromEntityAssembler;
import com.mineguard.platform.iam.interfaces.rest.transform.MobileSignInCommandFromResourceAssembler;
import com.mineguard.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Mobile authentication endpoint consumed by the Flutter operator application. */
@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Mobile Authentication", description = "Operator (mobile) authentication")
public class MobileAuthenticationController {
    private final UserCommandService userCommandService;

    public MobileAuthenticationController(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Mobile sign-in",
            description = "Authenticates a field operator by worker id and returns {workerId, fullName, role, token}.")
    public ResponseEntity<?> signIn(@RequestBody MobileSignInResource resource) {
        var command = MobileSignInCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = userCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                pair -> MobileAuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(pair.getLeft(), pair.getRight()),
                HttpStatus.OK);
    }
}
