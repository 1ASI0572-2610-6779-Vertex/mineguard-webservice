package com.mineguard.platform.assets.interfaces.rest.transform;

import com.mineguard.platform.assets.domain.model.aggregates.CatalogSummary;
import com.mineguard.platform.assets.interfaces.rest.resources.CatalogSummaryResource;

public final class CatalogSummaryResourceFromEntityAssembler {
    private CatalogSummaryResourceFromEntityAssembler() {
    }

    public static CatalogSummaryResource toResourceFromEntity(CatalogSummary s) {
        return new CatalogSummaryResource(s.getId(), s.getDriversTotal(), s.getDriversInactive(),
                s.getVehiclesTotal(), s.getVehiclesMaintenance(), s.getSupervisorsTotal(), s.getSupervisorsLocked());
    }
}
