package com.mineguard.platform.iam.domain.model.valueobjects;

/**
 * Enumerates the roles available in the MineGuard platform.
 *
 * <p>Roles map to the personas described in the user stories: platform
 * administrators, operation supervisors, and field operators (drivers).</p>
 */
public enum Roles {
    ROLE_ADMINISTRATOR,
    ROLE_SUPERVISOR,
    ROLE_OPERATOR,
    ROLE_DRIVER
}
