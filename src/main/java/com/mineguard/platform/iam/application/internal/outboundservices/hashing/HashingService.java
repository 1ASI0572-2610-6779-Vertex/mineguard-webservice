package com.mineguard.platform.iam.application.internal.outboundservices.hashing;

/** Outbound service port for password hashing. */
public interface HashingService {
    String encode(CharSequence rawPassword);
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
