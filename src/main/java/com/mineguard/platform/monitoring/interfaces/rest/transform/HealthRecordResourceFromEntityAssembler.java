package com.mineguard.platform.monitoring.interfaces.rest.transform;

import com.mineguard.platform.monitoring.domain.model.aggregates.HeartRateReading;
import com.mineguard.platform.monitoring.interfaces.rest.resources.HealthRecordResource;

public final class HealthRecordResourceFromEntityAssembler {
    private HealthRecordResourceFromEntityAssembler() {
    }

    public static HealthRecordResource toResourceFromEntity(HeartRateReading r) {
        return new HealthRecordResource(r.getId(), r.getDeviceId(), r.getBpm(), r.getCreatedAt());
    }
}
