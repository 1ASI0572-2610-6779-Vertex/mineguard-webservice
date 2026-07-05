package com.mineguard.platform.monitoring.interfaces.rest.transform;

import com.mineguard.platform.monitoring.domain.model.aggregates.Sensor;
import com.mineguard.platform.monitoring.interfaces.rest.resources.SensorResource;

public final class SensorResourceFromEntityAssembler {
    private SensorResourceFromEntityAssembler() {
    }

    public static SensorResource toResourceFromEntity(Sensor sensor) {
        return new SensorResource(
                sensor.getId(),
                sensor.getVehicleId(),
                sensor.getSensorType(),
                sensor.getDeviceId(),
                sensor.getStatus(),
                sensor.getCompanyId());
    }
}