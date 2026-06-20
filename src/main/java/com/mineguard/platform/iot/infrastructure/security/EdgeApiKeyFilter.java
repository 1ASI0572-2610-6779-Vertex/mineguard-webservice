package com.mineguard.platform.iot.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Machine-to-machine authentication filter for the IoT telemetry routes
 * ({@code /api/v1/iot/**}).
 *
 * <p>Validates the {@code X-API-Key} request header against the pre-shared key
 * configured via {@code ${EDGE_DEFAULT_API_KEY}} in {@code application.properties}.
 * Requests with a missing or incorrect key are short-circuited with
 * {@code 401 Unauthorized} before reaching any controller.</p>
 *
 * <p>This filter is intentionally separate from the JWT {@code BearerAuthorizationRequestFilter}:
 * edge devices never issue JWTs, so no Spring Security authentication object
 * is populated in the security context — the filter only acts as a gate.</p>
 */
@Component
public class EdgeApiKeyFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(EdgeApiKeyFilter.class);
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String IOT_PATH_PREFIX = "/api/v1/iot/";

    @Value("${edge.device.default.api-key}")
    private String validApiKey;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith(IOT_PATH_PREFIX);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var providedKey = request.getHeader(API_KEY_HEADER);
        if (providedKey == null || !providedKey.equals(validApiKey)) {
            LOGGER.warn("Rejected IoT request from {} — invalid or missing X-API-Key",
                    request.getRemoteAddr());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Missing or invalid X-API-Key\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
