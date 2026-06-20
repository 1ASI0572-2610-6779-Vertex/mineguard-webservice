package com.mineguard.platform.iam.interfaces.rest;

import com.mineguard.platform.iam.application.commandservices.UserCommandService;
import com.mineguard.platform.iam.domain.model.commands.ChangePasswordCommand;
import com.mineguard.platform.iam.domain.model.commands.ResetPasswordCommand;
import com.mineguard.platform.iam.domain.repositories.UserRepository;
import com.mineguard.platform.iam.interfaces.rest.resources.ChangePasswordResource;
import com.mineguard.platform.iam.interfaces.rest.resources.ForgotPasswordRequest;
import com.mineguard.platform.iam.interfaces.rest.resources.SignInResource;
import com.mineguard.platform.iam.interfaces.rest.resources.SignUpResource;
import com.mineguard.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.mineguard.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.mineguard.platform.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.mineguard.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import com.mineguard.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Manages the User resource and its password sub-resources.
 *
 * <p>Sign-in (session creation) is intentionally kept in a separate controller
 * ({@link MobileAuthenticationController} for mobile, and the web session endpoint below)
 * because authentication produces a Session, not a User.
 */
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users & Sessions (Web)", description = "User registration, web session creation, and password management. " +
        "All endpoints that create or mutate the User resource, or produce a web JWT session, are grouped here. " +
        "The MineGuard security model uses stateless JWT tokens: a successful sign-in (POST /api/v1/sessions) " +
        "returns a signed JWT that must be sent as `Authorization: Bearer <token>` on every subsequent request. " +
        "Tokens do not expire on the server side — revocation is handled by password change.")
public class AuthenticationController {

    private final UserCommandService userCommandService;
    private final UserRepository userRepository;

    public AuthenticationController(UserCommandService userCommandService, UserRepository userRepository) {
        this.userCommandService = userCommandService;
        this.userRepository = userRepository;
    }

    // =========================================================================
    // Sessions — web channel
    // =========================================================================

    @PostMapping("/sessions")
    @Operation(
            summary = "Create a web session (sign-in)",
            description = "Authenticates a web user (Supervisor or Admin) by username and password. " +
                    "On success, returns `{ id, username, token, role }` where `token` is a signed JWT. " +
                    "The token must be included as `Authorization: Bearer <token>` in all subsequent requests. " +
                    "No JWT is required to call this endpoint (it is public). " +
                    "Failed credentials return 401 — the response deliberately omits whether the username " +
                    "or the password was wrong to prevent user enumeration.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session created — JWT returned"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<?> signIn(@RequestBody SignInResource resource) {
        var command = SignInCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = userCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                pair -> AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(
                        pair.getLeft(), pair.getRight()),
                HttpStatus.OK);
    }

    // =========================================================================
    // Users — registration
    // =========================================================================

    @PostMapping("/users")
    @Operation(
            summary = "Create a user (sign-up)",
            description = "Registers a new MineGuard user. The role assigned depends on the `role` field in the request body: " +
                    "SUPERVISOR creates a supervisor account linked to an existing company; " +
                    "DRIVER accounts are created via POST /api/v1/drivers (not this endpoint). " +
                    "Returns `{ id, username }` — no JWT is issued on sign-up; " +
                    "the user must call POST /api/v1/sessions to authenticate after registration. " +
                    "No JWT is required to call this endpoint (it is public).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or username already taken"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    public ResponseEntity<?> signUp(@RequestBody SignUpResource resource) {
        var command = SignUpCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = userCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                UserResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }

    // =========================================================================
    // Password resets — unauthenticated flow
    // =========================================================================

    @PostMapping("/users/password-resets")
    @Operation(
            summary = "Request a password reset",
            description = "Creates a password-reset request for the given email address. " +
                    "If the email is registered, a temporary password is generated and sent via Brevo SMTP. " +
                    "The response is always 200 OK regardless of whether the email exists — " +
                    "this prevents user enumeration attacks (the caller cannot distinguish 'email not found' " +
                    "from 'email found and email sent'). " +
                    "No JWT is required (this endpoint is public — the user has lost access).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reset initiated (response is identical whether the email exists or not)"),
            @ApiResponse(responseCode = "400", description = "Malformed email address")
    })
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody ForgotPasswordRequest request) {
        userCommandService.handle(new ResetPasswordCommand(request.email()));
        return ResponseEntity.ok(
                "{\"message\":\"Si el correo está registrado, se han enviado las instrucciones de restablecimiento.\"}");
    }

    // =========================================================================
    // Me — authenticated password change
    // =========================================================================

    @PatchMapping("/users/me/password")
    @Operation(
            summary = "Change the authenticated user's password",
            description = "Performs a partial update of the currently authenticated user's password field. " +
                    "PATCH is used (not PUT) because only the `password` field is being replaced — " +
                    "no other user fields are affected. " +
                    "Requires a valid JWT (`Authorization: Bearer <token>`). " +
                    "After a successful change, the `requiresPasswordChange` flag is cleared, " +
                    "meaning the user will no longer be forced to change their password on next login. " +
                    "The current password is not required in the request body — access to a valid JWT " +
                    "is considered sufficient proof of identity for this operation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error — new password does not meet requirements"),
            @ApiResponse(responseCode = "401", description = "JWT missing, expired, or invalid"),
            @ApiResponse(responseCode = "404", description = "Authenticated user not found (should not happen in normal flow)")
    })
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordResource resource,
                                             @AuthenticationPrincipal UserDetails currentUser) {
        var user = userRepository.findByUsername(currentUser.getUsername());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var command = new ChangePasswordCommand(user.get().getId(), resource.newPassword());
        var result = userCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                UserResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }
}
