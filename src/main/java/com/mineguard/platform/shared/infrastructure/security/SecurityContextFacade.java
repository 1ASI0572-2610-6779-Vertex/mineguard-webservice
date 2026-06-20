package com.mineguard.platform.shared.infrastructure.security;

import com.mineguard.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Provides convenient access to the authenticated tenant's identity from any Spring-managed bean.
 *
 * <p>Returns {@code null} when there is no authenticated principal or the principal
 * does not carry a {@code companyId} (e.g., during seeding or unauthenticated requests).
 * Callers must decide whether {@code null} means "show all" (super-admin) or "deny".</p>
 */
@Component
public class SecurityContextFacade {

    /** Returns the companyId of the currently authenticated user, or {@code null} if unavailable. */
    public Long currentCompanyId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        if (auth.getPrincipal() instanceof UserDetailsImpl details) {
            return details.getCompanyId();
        }
        return null;
    }

    /** Returns the username of the currently authenticated user, or {@code null} if unavailable. */
    public String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        if (auth.getPrincipal() instanceof UserDetailsImpl details) {
            return details.getUsername();
        }
        return null;
    }
}
