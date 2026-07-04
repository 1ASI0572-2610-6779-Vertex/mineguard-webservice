package com.mineguard.platform.iot.infrastructure.security;

import com.mineguard.platform.subscriptions.domain.model.aggregates.Company;
import com.mineguard.platform.subscriptions.domain.repositories.CompanyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Machine-to-machine authentication filter for the IoT telemetry route
 * ({@code /api/v1/telemetry}).
 *
 * <p>Validates the {@code X-API-Key} request header against the per-company key
 * generated at company registration time, not a single platform-wide secret.
 * On success, the resolved {@code companyId} is stored
 * as a request attribute ({@link #COMPANY_ID_ATTRIBUTE}) so downstream controllers/services
 * can scope sensor/device lookups to that tenant — this is what prevents a
 * {@code device_id} collision between two companies from leaking telemetry across tenants.</p>
 *
 * <p>This filter is intentionally separate from the JWT {@code BearerAuthorizationRequestFilter}:
 * edge devices never issue JWTs, so no Spring Security authentication object
 * is populated in the security context — the filter only acts as a gate.</p>
 */
@Component
public class EdgeApiKeyFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(EdgeApiKeyFilter.class);
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String IOT_PATH_PREFIX = "/api/v1/telemetry";
    /** Request attribute carrying the tenant resolved from the X-API-Key, for downstream controllers. */
    public static final String COMPANY_ID_ATTRIBUTE = "mineguard.edge.companyId";

    private final CompanyRepository companyRepository;

    public EdgeApiKeyFilter(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith(IOT_PATH_PREFIX);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var providedKey = request.getHeader(API_KEY_HEADER);
        Optional<Company> company = providedKey == null
                ? Optional.empty() : companyRepository.findByEdgeApiKey(providedKey);
        if (company.isEmpty()) {
            LOGGER.warn("Rejected IoT request from {} — invalid or missing X-API-Key",
                    request.getRemoteAddr());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Missing or invalid X-API-Key\"}");
            return;
        }
        request.setAttribute(COMPANY_ID_ATTRIBUTE, company.get().getId());
        filterChain.doFilter(request, response);
    }
}
