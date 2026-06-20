package com.mineguard.platform.shared.domain.utils;

/**
 * Generates deterministic, role-scoped professional identifiers for platform users.
 *
 * <p>Format: {@code [PREFIX]-[companyId]-[zero-padded-sequence]}.
 * The sequence is derived from the caller passing the current count of users
 * with the same prefix, so concurrency safety depends on the calling service
 * using a DB-level unique constraint on the username column.</p>
 *
 * <p>Examples: {@code ADM-3-001}, {@code SUP-3-001}, {@code CDT-3-004}.</p>
 */
public final class UsernameGenerator {

    private UsernameGenerator() {}

    public static String forAdmin(Long companyId, long currentCount) {
        return "ADM-%d-%03d".formatted(companyId, currentCount + 1);
    }

    public static String forSupervisor(Long companyId, long currentCount) {
        return "SUP-%d-%03d".formatted(companyId, currentCount + 1);
    }

    public static String forDriver(Long companyId, long currentCount) {
        return "CDT-%d-%03d".formatted(companyId, currentCount + 1);
    }

    /** Returns the prefix used to query the current count for admins of a company. */
    public static String adminPrefix(Long companyId) {
        return "ADM-%d-".formatted(companyId);
    }

    /** Returns the prefix used to query the current count for supervisors of a company. */
    public static String supervisorPrefix(Long companyId) {
        return "SUP-%d-".formatted(companyId);
    }

    /** Returns the prefix used to query the current count for drivers of a company. */
    public static String driverPrefix(Long companyId) {
        return "CDT-%d-".formatted(companyId);
    }
}
