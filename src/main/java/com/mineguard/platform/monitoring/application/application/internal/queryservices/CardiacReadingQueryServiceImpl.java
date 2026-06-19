package com.mineguard.platform.monitoring.application.application.internal.queryservices;

import com.mineguard.platform.assets.domain.repositories.DriverRepository;
import com.mineguard.platform.assets.domain.repositories.VehicleRepository;
import com.mineguard.platform.monitoring.application.queryservices.CardiacReadingQueryService;
import com.mineguard.platform.monitoring.domain.model.aggregates.CardiacReading;
import com.mineguard.platform.monitoring.domain.model.queries.GetAllCardiacReadingsQuery;
import com.mineguard.platform.monitoring.domain.repositories.SensorReadingRepository;
import com.mineguard.platform.monitoring.domain.repositories.SensorRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardiacReadingQueryServiceImpl implements CardiacReadingQueryService {
    private final SensorReadingRepository sensorReadingRepository;
    private final SensorRepository sensorRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public CardiacReadingQueryServiceImpl(SensorReadingRepository sensorReadingRepository, SensorRepository sensorRepository,
                                          VehicleRepository vehicleRepository, DriverRepository driverRepository) {
        this.sensorReadingRepository = sensorReadingRepository;
        this.sensorRepository = sensorRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    public List<CardiacReading> handle(GetAllCardiacReadingsQuery query) {

        var sensorsById = sensorRepository.findAll().stream()
                .collect(Collectors.toMap(s -> s.getId(), s -> s, (a, b) -> a));

        var vehiclesById = vehicleRepository.findAll().stream()
                .collect(Collectors.toMap(v -> v.getId(), v -> v, (a, b) -> a));

        var driversById = driverRepository.findAll().stream()
                .collect(Collectors.toMap(d -> d.getId(), d -> d, (a, b) -> a));

        var latestReadingByDriver = sensorReadingRepository.findAll().stream()
                .filter(r -> "heart_rate".equalsIgnoreCase(r.getReadingType()))
                .collect(Collectors.toMap(

                        r -> {
                            var sensor = sensorsById.get(r.getSensorId());
                            var vehicle = sensor == null ? null : vehiclesById.get(sensor.getVehicleId());
                            return vehicle == null ? -1L : vehicle.getDriverId();
                        },

                        r -> r,

                        (existing, replacement) ->
                                replacement.getTimestamp().compareTo(existing.getTimestamp()) > 0
                                        ? replacement
                                        : existing
                ));

        return latestReadingByDriver.values().stream()

                .map(r -> {
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

                .sorted(
                        Comparator.comparingInt(CardiacReading::getHeartRate)
                                .reversed()
                )

                .toList();
    }

    private String cardiacStatus(double bpm) {
        if (bpm >= 140) return "critical";
        if (bpm >= 110) return "warning";
        return "normal";
    }
}
