package com.mineguard.platform.iam.interfaces.rest.transform;

import com.mineguard.platform.iam.domain.model.aggregates.User;

/** Helper to convert internal role names to the labels the frontends expect. */
public final class RoleLabel {
    private RoleLabel() {
    }

    /** Web label, e.g. ROLE_ADMINISTRATOR -> "Administrator". */
    public static String web(User user) {
        String name = stripPrefix(user.getPrimaryRoleName());
        if (name.isEmpty()) return "Operator";
        return name.charAt(0) + name.substring(1).toLowerCase();
    }

    /** Mobile label (lowercase), e.g. ROLE_OPERATOR -> "operator". */
    public static String mobile(User user) {
        return stripPrefix(user.getPrimaryRoleName()).toLowerCase();
    }

    private static String stripPrefix(String roleName) {
        return roleName.startsWith("ROLE_") ? roleName.substring(5) : roleName;
    }
}
