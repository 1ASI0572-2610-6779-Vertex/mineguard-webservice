package com.mineguard.platform.analytics.interfaces.rest.transform;

import com.mineguard.platform.analytics.domain.model.aggregates.CompanyKpis;
import com.mineguard.platform.analytics.interfaces.rest.resources.CompanyKpisResource;

public final class CompanyKpisResourceFromEntityAssembler {
    private CompanyKpisResourceFromEntityAssembler() {
    }

    public static CompanyKpisResource toResourceFromEntity(CompanyKpis k) {
        return new CompanyKpisResource(k.getCompanyId(), k.getDriversTotal(), k.getDriversInactive(),
                k.getVehiclesTotal(), k.getVehiclesOperational(), k.getVehiclesMaintenance(), k.getVehiclesAlert(),
                k.getVehiclesOperationalPercent(), k.getSupervisorsTotal(), k.getSupervisorsLocked(),
                k.getActiveSensors(), k.getTotalSensors(), k.getCriticalAlerts(), k.getFatigueEvents());
    }
}
