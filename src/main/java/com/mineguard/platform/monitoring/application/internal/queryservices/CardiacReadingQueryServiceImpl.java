package com.mineguard.platform.monitoring.application.internal.queryservices;

import com.mineguard.platform.assets.domain.model.aggregates.Driver;
import com.mineguard.platform.assets.domain.model.aggregates.Vehicle;
import com.mineguard.platform.assets.domain.repositories.DriverRepository;
import com.mineguard.platform.assets.domain.repositories.VehicleRepository;
import com.mineguard.platform.monitoring.application.queryservices.CardiacReadingQueryService;
import com.mineguard.platform.monitoring.domain.model.aggregates.CardiacReading;
import com.mineguard.platform.monitoring.domain.model.aggregates.Sensor;
import com.mineguard.platform.monitoring.domain.model.queries.GetAllCardiacReadingsQuery;
import com.mineguard.platform.monitoring.domain.repositories.SensorReadingRepository;
import com.mineguard.platform.monitoring.domain.repositories.SensorRepository;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CardiacReadingQueryServiceImpl implements CardiacReadingQueryService {

    private final SensorReadingRepository sensorReadingRepository;
    private final SensorRepository sensorRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final SecurityContextFacade securityContext;

    public CardiacReadingQueryServiceImpl(SensorReadingRepository sensorReadingRepository,
                                          SensorRepository sensorRepository,
                                          VehicleRepository vehicleRepository,
                                          DriverRepository driverRepository,
                                          SecurityContextFacade securityContext) {
        this.sensorReadingRepository = sensorReadingRepository;
        this.sensorRepository = sensorRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.securityContext = securityContext;
    }

    @Override
    public List<CardiacReading> handle(GetAllCardiacReadingsQuery query) {
        var companyId = securityContext.currentCompanyId();
        if (companyId == null) return List.of();

        // Solo vehículos y conductores del tenant
        Map<Long, Vehicle> vehiclesById = vehicleRepository.findAllByCompanyId(companyId).stream()
                .collect(Collectors.toMap(Vehicle::getId, v -> v, (a, b) -> a));
        Map<Long, Driver> driversById = driverRepository.findAllByCompanyId(companyId).stream()
                .collect(Collectors.toMap(Driver::getId, d -> d, (a, b) -> a));

        // Sensores del tenant: solo los montados en vehículos del tenant
        Set<Long> tenantVehicleIds = vehiclesById.keySet();
        Map<Long, Sensor> sensorsById = sensorRepository.findAll().stream()
                .filter(s -> s.getVehicleId() != null && tenantVehicleIds.contains(s.getVehicleId()))
                .collect(Collectors.toMap(Sensor::getId, s -> s, (a, b) -> a));

        var latestReadingByDriver = sensorReadingRepository.findAll().stream()
                .filter(r -> "heart_rate".equalsIgnoreCase(r.getReadingType()))
                .filter(r -> sensorsById.containsKey(r.getSensorId())) // solo lecturas de sensores del tenant
                .collect(Collectors.toMap(
                        r -> {
                            var sensor = sensorsById.get(r.getSensorId());
                            var vehicle = sensor == null ? null : vehiclesById.get(sensor.getVehicleId());
                            return vehicle == null ? -1L : vehicle.getDriverId();
                        },
                        r -> r,
                        (existing, replacement) ->
                                replacement.getTimestamp().compareTo(existing.getTimestamp()) > 0
                                        ? replacement : existing
                ));

        return latestReadingByDriver.entrySet().stream()
                .filter(e -> e.getKey() != -1L) // descartar lecturas sin conductor asignado
                .map(e -> {
                    var r = e.getValue();
                    var sensor = sensorsById.get(r.getSensorId());
                    var vehicle = sensor == null ? null : vehiclesById.get(sensor.getVehicleId());
                    var driver = vehicle == null ? null : driversById.get(vehicle.getDriverId());
                    var reading = new CardiacReading(
                            driver == null ? "" : driver.getFullName(),
                            vehicle == null ? "" : vehicle.getCode(),
                            (int) Math.round(r.getValue()),
                            cardiacStatus(r.getValue())
                    );
                    reading.setId(r.getId());
                    return reading;
                })
                .sorted(Comparator.comparingInt(CardiacReading::getHeartRate).reversed())
                .toList();
    }

    private String cardiacStatus(double bpm) {
        if (bpm >= 140) return "critical";
        if (bpm >= 110) return "warning";
        return "normal";
    }
}
