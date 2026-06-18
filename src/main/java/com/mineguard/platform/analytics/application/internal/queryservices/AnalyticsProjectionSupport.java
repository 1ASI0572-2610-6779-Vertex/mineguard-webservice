package com.mineguard.platform.analytics.application.internal.queryservices;

import com.mineguard.platform.analytics.domain.model.aggregates.PerformanceMetric;
import com.mineguard.platform.analytics.infrastructure.persistence.jpa.entities.PerformanceMetricPersistenceEntity;
import com.mineguard.platform.analytics.infrastructure.persistence.jpa.repositories.PerformanceMetricPersistenceRepository;
import com.mineguard.platform.assets.domain.model.aggregates.Driver;
import com.mineguard.platform.assets.domain.model.aggregates.Trip;
import com.mineguard.platform.assets.domain.model.aggregates.Vehicle;
import com.mineguard.platform.assets.domain.model.valueobjects.VehicleStatus;
import com.mineguard.platform.assets.domain.repositories.DriverRepository;
import com.mineguard.platform.assets.domain.repositories.TripRepository;
import com.mineguard.platform.assets.domain.repositories.VehicleRepository;
import com.mineguard.platform.iam.domain.repositories.SupervisorRepository;
import com.mineguard.platform.iam.domain.repositories.UserRepository;
import com.mineguard.platform.monitoring.domain.model.aggregates.Alert;
import com.mineguard.platform.monitoring.domain.model.aggregates.Incident;
import com.mineguard.platform.monitoring.domain.model.aggregates.Sensor;
import com.mineguard.platform.monitoring.domain.repositories.AlertRepository;
import com.mineguard.platform.monitoring.domain.repositories.IncidentRepository;
import com.mineguard.platform.monitoring.domain.repositories.SensorReadingRepository;
import com.mineguard.platform.monitoring.domain.repositories.SensorRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
class AnalyticsProjectionSupport {
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter TIME_SECONDS = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;
    private final SensorRepository sensorRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final AlertRepository alertRepository;
    private final IncidentRepository incidentRepository;
    private final PerformanceMetricPersistenceRepository performanceMetricRepository;
    private final UserRepository userRepository;
    private final SupervisorRepository supervisorRepository;

    AnalyticsProjectionSupport(DriverRepository driverRepository, VehicleRepository vehicleRepository,
                               TripRepository tripRepository, SensorRepository sensorRepository,
                               SensorReadingRepository sensorReadingRepository, AlertRepository alertRepository,
                               IncidentRepository incidentRepository,
                               PerformanceMetricPersistenceRepository performanceMetricRepository,
                               UserRepository userRepository, SupervisorRepository supervisorRepository) {
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.tripRepository = tripRepository;
        this.sensorRepository = sensorRepository;
        this.sensorReadingRepository = sensorReadingRepository;
        this.alertRepository = alertRepository;
        this.incidentRepository = incidentRepository;
        this.performanceMetricRepository = performanceMetricRepository;
        this.userRepository = userRepository;
        this.supervisorRepository = supervisorRepository;
    }

    List<Driver> drivers() { return driverRepository.findAll(); }
    List<Vehicle> vehicles() { return vehicleRepository.findAll(); }
    List<Trip> trips() { return tripRepository.findAll(); }
    List<Sensor> sensors() { return sensorRepository.findAll(); }
    List<Alert> alerts() { return alertRepository.findAll(); }
    List<Incident> incidents() { return incidentRepository.findAll(); }
    long usersCount() { return userRepository.findAll().size(); }
    long supervisorsCount() { return supervisorRepository.findAll().size(); }
    long lockedSupervisorsCount() {
        return supervisorRepository.findAll().stream()
                .filter(s -> s.getAccessStatus() != null && "locked".equals(s.getAccessStatus().toSerialized()))
                .count();
    }
    long heartRateReadingCount() {
        return sensorReadingRepository.findAll().stream()
                .filter(r -> "heart_rate".equalsIgnoreCase(r.getReadingType()))
                .count();
    }

    List<PerformanceMetric> metrics() {
        return performanceMetricRepository.findAll().stream().map(this::toMetric).toList();
    }

    Map<Long, Driver> driversById() {
        return drivers().stream().collect(Collectors.toMap(Driver::getId, Function.identity(), (a, b) -> a));
    }

    Map<Long, Vehicle> vehiclesById() {
        return vehicles().stream().collect(Collectors.toMap(Vehicle::getId, Function.identity(), (a, b) -> a));
    }

    Map<Long, Trip> tripsById() {
        return trips().stream().collect(Collectors.toMap(Trip::getId, Function.identity(), (a, b) -> a));
    }

    Map<Long, Alert> alertsById() {
        return alerts().stream().collect(Collectors.toMap(Alert::getId, Function.identity(), (a, b) -> a));
    }

    Optional<Vehicle> vehicleForMetric(PerformanceMetric metric) {
        return vehicles().stream().filter(v -> v.getId().equals(metric.getVehicleId())).findFirst();
    }

    Optional<Driver> driverForMetric(PerformanceMetric metric) {
        return drivers().stream().filter(d -> d.getId().equals(metric.getDriverId())).findFirst();
    }

    Optional<Trip> tripForAlert(Alert alert) {
        return trips().stream().filter(t -> t.getId().equals(alert.getTripId())).findFirst();
    }

    Optional<Vehicle> vehicleForAlert(Alert alert) {
        var tripsById = tripsById();
        var vehiclesById = vehiclesById();
        var trip = tripsById.get(alert.getTripId());
        return trip == null ? Optional.empty() : Optional.ofNullable(vehiclesById.get(trip.getVehicleId()));
    }

    Optional<Driver> driverForAlert(Alert alert) {
        var tripsById = tripsById();
        var driversById = driversById();
        var trip = tripsById.get(alert.getTripId());
        return trip == null ? Optional.empty() : Optional.ofNullable(driversById.get(trip.getDriverId()));
    }

    long activeVehiclesCount() {
        return vehicles().stream().filter(v -> v.getStatus() == VehicleStatus.OPERATIONAL).count();
    }

    String alertCode(Alert alert) {
        return alert.getCode() != null ? alert.getCode() : "AL - " + String.format("%03d", alert.getId());
    }

    String rawType(Alert alert) {
        if (alert.getRawType() != null && !alert.getRawType().isBlank()) return alert.getRawType();
        return alert.getType() == null ? "" : alert.getType().toSerialized();
    }

    String category(Alert alert) {
        return switch (rawType(alert)) {
            case "proximity_collision" -> "Proximity Collision";
            case "restricted_zone_entry" -> "Restricted Zone Entry";
            case "high_heart_rate" -> "High Heart Rate";
            case "fatigue_risk" -> "Fatigue Risk";
            case "speed_excess" -> "Speed Excess";
            case "connection_lost" -> "Connection Lost";
            case "sensor_maintenance" -> "Sensor Maintenance";
            default -> titleCase(rawType(alert).replace('_', ' '));
        };
    }

    String incidentLabel(Alert alert) {
        return switch (rawType(alert)) {
            case "proximity_collision" -> "Proximidad / Colision";
            case "restricted_zone_entry" -> "Incursion en Zona Restringida";
            case "high_heart_rate" -> "Ritmo Cardiaco Elevado";
            case "fatigue_risk" -> "Fatiga";
            case "connection_lost" -> "Perdida de Conexion";
            default -> category(alert);
        };
    }

    String criticalityLabel(String severity) {
        return switch (severity == null ? "" : severity.toLowerCase()) {
            case "high" -> "Critica";
            case "medium" -> "Advertencia";
            default -> "Sistema";
        };
    }

    String route(Trip trip) {
        return trip == null || trip.getId() == null ? "Ruta N/D" : "Ruta " + trip.getId();
    }

    String time(String timestamp) {
        return parse(timestamp).map(TIME::format).orElse("");
    }

    String timeSeconds(String timestamp) {
        return parse(timestamp).map(TIME_SECONDS::format).orElse("");
    }

    String date(String timestamp) {
        return parse(timestamp).map(DATE::format).map(String::toLowerCase).orElse("");
    }

    String hour(String timestamp) {
        return parse(timestamp).map(t -> String.format("%02d:00", t.getHour())).orElse("");
    }

    Comparator<Alert> newestAlertsFirst() {
        return Comparator.comparing((Alert a) -> parse(a.getOccurredAt()).orElse(LocalDateTime.MIN)).reversed();
    }

    private Optional<LocalDateTime> parse(String timestamp) {
        try {
            return timestamp == null || timestamp.isBlank() ? Optional.empty() : Optional.of(LocalDateTime.parse(timestamp));
        } catch (RuntimeException ignored) {
            return Optional.empty();
        }
    }

    private PerformanceMetric toMetric(PerformanceMetricPersistenceEntity e) {
        var metric = new PerformanceMetric(e.getDriverId(), e.getTripId(), e.getVehicleId(), e.getFatigueEvents(),
                e.getAlertsCount(), e.getAverageHeartRate(), e.getRiskScore(), e.getCalculatedAt());
        metric.setId(e.getId());
        return metric;
    }

    private String titleCase(String value) {
        var words = value.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            if (!words[i].isBlank()) {
                words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
            }
        }
        return String.join(" ", words);
    }
}
