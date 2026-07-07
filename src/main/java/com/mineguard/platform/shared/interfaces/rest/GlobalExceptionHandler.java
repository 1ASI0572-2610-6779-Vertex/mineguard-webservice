package com.mineguard.platform.shared.interfaces.rest;

import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.interfaces.rest.resources.ErrorResource;
import com.mineguard.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Global exception handler for the REST API.
 * Provides centralized exception handling, ensuring all unhandled exceptions
 * are translated to consistent HTTP responses via the shared error assembly pattern.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String MESSAGES_BASENAME = "messages";

    /**
     * Handles validation exceptions from Spring's request body validation.
     *
     * @param ex the validation exception from {@code @Valid} binding
     * @return error response with BAD_REQUEST status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        var fieldErrors = ex.getBindingResult().getFieldErrors();
        var validationPrefix = resolveMessageOrDefault("validation.field.prefix", "Field");
        var errorDetails = fieldErrors.isEmpty()
                ? resolveMessageOrDefault("validation.request.failed", "Request validation failed")
                : fieldErrors.stream()
                .map(error -> "%s %s: %s".formatted(
                        validationPrefix,
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .reduce((a, b) -> a + "; " + b)
                .orElse(resolveMessageOrDefault("validation.request.failed", "Request validation failed"));

        var applicationError = ApplicationError.validationError("request-body", errorDetails);
        return ErrorResponseAssembler.toErrorResponseFromApplicationError(applicationError);
    }

    /**
     * Handles malformed request bodies — including unrecognized JSON properties on DTOs annotated
     * with {@code @JsonIgnoreProperties(ignoreUnknown = false)} (e.g. a client trying to smuggle
     * {@code username}/{@code password} into a create-driver/create-supervisor payload). Without
     * this handler the exception would fall through to {@link #handleGenericException} and
     * incorrectly report 500 for what is actually a 400-level client error.
     *
     * @param ex the message-not-readable exception (bad JSON, unknown property, type mismatch, etc.)
     * @return error response with BAD_REQUEST status
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        var applicationError = ApplicationError.validationError(
                resolveMessageOrDefault("validation.request.argument", "request-argument"),
                resolveMessageOrDefault("validation.request.failed", "Request validation failed")
        );
        return ErrorResponseAssembler.toErrorResponseFromApplicationError(applicationError);
    }

    /**
     * Handles invalid request arguments such as malformed path or payload values.
     *
     * @param ex the illegal argument exception
     * @return error response with BAD_REQUEST status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        var applicationError = ApplicationError.validationError(
                resolveMessageOrDefault("validation.request.argument", "request-argument"),
                ex.getMessage() != null ? ex.getMessage()
                        : resolveMessageOrDefault("validation.request.failed", "Request validation failed")
        );
        return ErrorResponseAssembler.toErrorResponseFromApplicationError(applicationError);
    }

    /**
     * Handles method-security authorization failures. A {@code @PreAuthorize} denial throws
     * {@link AccessDeniedException} (Spring Security's {@code AuthorizationDeniedException} is a
     * subclass) from inside the controller invocation, where Spring MVC's resolver catches it
     * <em>before</em> the security filter chain can translate it — so without this handler it would
     * fall through to {@link #handleGenericException} and be reported as 500 instead of 403.
     *
     * @param ex the access-denied exception
     * @return error response with FORBIDDEN status
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResource> handleAccessDenied(AccessDeniedException ex) {
        var message = resolveMessageOrDefault("error.access-denied.message",
                "Access denied — you do not have permission to perform this action");
        return new ResponseEntity<>(new ErrorResource("ACCESS_DENIED", message, null), HttpStatus.FORBIDDEN);
    }

    /**
     * Last-resort handler for any otherwise unhandled exception.
     *
     * @param ex the exception
     * @return error response with INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        var applicationError = ApplicationError.unexpected(
                "request-processing",
                ex.getMessage() != null ? ex.getMessage() : "Unhandled error");
        return ErrorResponseAssembler.toErrorResponseFromApplicationError(applicationError);
    }

    private static String resolveMessageOrDefault(String key, String defaultValue) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(MESSAGES_BASENAME, LocaleContextHolder.getLocale());
            return bundle.containsKey(key) ? bundle.getString(key) : defaultValue;
        } catch (MissingResourceException ex) {
            return defaultValue;
        }
    }
}
