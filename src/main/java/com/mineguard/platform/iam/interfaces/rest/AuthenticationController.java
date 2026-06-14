package com.mineguard.platform.iam.interfaces.rest;

import com.mineguard.platform.iam.application.commandservices.UserCommandService;
import com.mineguard.platform.iam.interfaces.rest.resources.SignInResource;
import com.mineguard.platform.iam.interfaces.rest.resources.SignUpResource;
import com.mineguard.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.mineguard.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.mineguard.platform.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.mineguard.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import com.mineguard.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Web authentication endpoints consumed by the MineGuard Angular application. */
@RestController
@RequestMapping(value = "/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Web authentication and user registration")
public class AuthenticationController {
    private final UserCommandService userCommandService;

    public AuthenticationController(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Web sign-in", description = "Authenticates a user and returns {id, username, token, role}.")
    public ResponseEntity<?> signIn(@RequestBody SignInResource resource) {
        var command = SignInCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = userCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                pair -> AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(pair.getLeft(), pair.getRight()),
                HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    @Operation(summary = "Web sign-up", description = "Registers a new user and returns {id, username}.")
    public ResponseEntity<?> signUp(@RequestBody SignUpResource resource) {
        var command = SignUpCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = userCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                UserResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }
}
