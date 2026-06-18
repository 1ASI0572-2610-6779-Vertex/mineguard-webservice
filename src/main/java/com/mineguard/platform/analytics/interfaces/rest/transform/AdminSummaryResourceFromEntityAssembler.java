package com.mineguard.platform.analytics.interfaces.rest.transform;

import com.mineguard.platform.analytics.domain.model.aggregates.AdminSummary;
import com.mineguard.platform.analytics.interfaces.rest.resources.AdminSummaryResource;

public final class AdminSummaryResourceFromEntityAssembler {
    private AdminSummaryResourceFromEntityAssembler() {
    }

    public static AdminSummaryResource toResourceFromEntity(AdminSummary d) {
        return new AdminSummaryResource(d.getId(), d.getActiveSensors(), d.getTotalSensors(), d.getLockedAccounts(), d.getRegisteredAssets());
    }
}
