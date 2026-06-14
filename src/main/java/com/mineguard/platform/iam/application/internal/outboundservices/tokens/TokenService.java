package com.mineguard.platform.iam.application.internal.outboundservices.tokens;

/** Outbound service port for token generation and validation. */
public interface TokenService {
    String generateToken(String username);
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
}
