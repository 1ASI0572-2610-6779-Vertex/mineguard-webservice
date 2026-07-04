package com.mineguard.platform.monitoring.application.internal.queryservices;

import com.mineguard.platform.assets.domain.model.aggregates.Driver;
import com.mineguard.platform.assets.domain.repositories.DriverRepository;
import com.mineguard.platform.assets.domain.repositories.TripRepository;
import com.mineguard.platform.assets.domain.repositories.VehicleRepository;
import com.mineguard.platform.monitoring.application.queryservices.CardiacReadingQueryService;
import com.mineguard.platform.monitoring.domain.model.aggregates.CardiacReading;
import com.mineguard.platform.monitoring.domain.model.aggregates.Sensor;
import com.mineguard.platform.monitoring.domain.model.queries.GetCardiacReadingQuery;
import com.mineguard.platform.monitoring.domain.repositories.SensorReadingRepository;
import com.mineguard.platform.monitoring.domain.repositories.SensorRepository;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CardiacReadingQueryServiceImpl implements CardiacReadingQueryService {

    private final SensorReadingRepository sensorReadingRepository;
    private final SensorRepository sensorRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final TripRepository tripRepository;
    private final SecurityContextFacade securityContext;

    public CardiacReadingQueryServiceImpl(SensorReadingRepository sensorReadingRepository,
                                          SensorRepository sensorRepository,
                                          VehicleRepository vehicleRepository,
                                          DriverRepository driverRepository,
                                          TripRepository tripRepository,
                                          SecurityContextFacade securityContext) {
        this.sensorReadingRepository = sensorReadingRepository;
        this.sensorRepository = sensorRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.tripRepository = tripRepository;
        this.securityContext = securityContext;
    }

    @Override
    public Optional<CardiacReading> handle(GetCardiacReadingQuery query) {
        var companyId = securityContext.currentCompanyId();
        if (companyId == null || query.sessionId() == null) return Optional.empty();

        // The session must exist and belong to the authenticated company — ownership enforced here,
        // not left to the caller, so /driving-sessions/{sessionId}/cardiac-readings can never leak another tenant's data.
        var tripOpt = tripRepository.findById(query.sessionId());
        if (tripOpt.isEmpty() || !companyId.equals(tripOpt.get().getCompanyId())) return Optional.empty();
        var trip = tripOpt.get();

        var vehicleOpt = vehicleRepository.findById(trip.getVehicleId());
        if (vehicleOpt.isEmpty()) return Optional.empty();
        var vehicle = vehicleOpt.get();
        var driverName = driverRepository.findById(trip.getDriverId()).map(Driver::getFullName).orElse("");

        // Sensors mounted on this specific session's vehicle only.
        Set<Long> sessionSensorIds = sensorRepository.findAll().stream()
                .filter(s -> vehicle.getId().equals(s.getVehicleId()))
                .map(Sensor::getId)
                .collect(Collectors.toSet());

        return sensorReadingRepository.findAll().stream()
                .filter(r -> "heart_rate".equalsIgnoreCase(r.getReadingType()))
                .filter(r -> sessionSensorIds.contains(r.getSensorId()))
                .max(Comparator.comparing(r -> r.getTimestamp()))
                .map(r -> {
                    var reading = new CardiacReading(driverName, vehicle.getCode(),
                            (int) Math.round(r.getValue()), cardiacStatus(r.getValue()));
                    reading.setId(r.getId());
                    return reading;
                });
    }

    private String cardiacStatus(double bpm) {
        if (bpm >= 140) return "critical";
        if (bpm >= 110) return "warning";
        return "normal";
    }
}
