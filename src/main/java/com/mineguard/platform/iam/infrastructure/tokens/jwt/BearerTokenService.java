package com.mineguard.platform.iam.infrastructure.tokens.jwt;

import com.mineguard.platform.iam.application.internal.outboundservices.tokens.TokenService;
import jakarta.servlet.http.HttpServletRequest;

/** JWT-specific extension of the token service port. */
public interface BearerTokenService extends TokenService {
    String getBearerTokenFrom(HttpServletRequest request);
}
