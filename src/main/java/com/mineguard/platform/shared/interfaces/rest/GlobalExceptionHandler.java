package com.mineguard.platform.shared.interfaces.rest;

import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
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
