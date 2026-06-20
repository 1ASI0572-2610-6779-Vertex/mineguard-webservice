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
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    private final SecurityContextFacade securityContext;

    AnalyticsProjectionSupport(DriverRepository driverRepository, VehicleRepository vehicleRepository,
                               TripRepository tripRepository, SensorRepository sensorRepository,
                               SensorReadingRepository sensorReadingRepository, AlertRepository alertRepository,
                               IncidentRepository incidentRepository,
                               PerformanceMetricPersistenceRepository performanceMetricRepository,
                               UserRepository userRepository, SupervisorRepository supervisorRepository,
                               SecurityContextFacade securityContext) {
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
        this.securityContext = securityContext;
    }

    // ── Métodos de colección base — todos filtrados por tenant ────────────────

    List<Driver> drivers() {
        var id = securityContext.currentCompanyId();
        return id != null ? driverRepository.findAllByCompanyId(id) : List.of();
    }

    List<Vehicle> vehicles() {
        var id = securityContext.currentCompanyId();
        return id != null ? vehicleRepository.findAllByCompanyId(id) : List.of();
    }

    List<Trip> trips() {
        var id = securityContext.currentCompanyId();
        return id != null ? tripRepository.findAllByCompanyId(id) : List.of();
    }

    List<Alert> alerts() {
        var id = securityContext.currentCompanyId();
        return id != null ? alertRepository.findAllByCompanyId(id) : List.of();
    }

    List<Incident> incidents() {
        var id = securityContext.currentCompanyId();
        return id != null ? incidentRepository.findAllByCompanyId(id) : List.of();
    }

    /**
     * Sensores no tienen companyId en su tabla. Se filtran indirectamente:
     * solo se exponen los sensores montados en vehículos que pertenecen al tenant.
     */
    List<Sensor> sensors() {
        Set<Long> tenantVehicleIds = vehicles().stream()
                .map(Vehicle::getId)
                .collect(Collectors.toSet());
        return sensorRepository.findAll().stream()
                .filter(s -> s.getVehicleId() != null && tenantVehicleIds.contains(s.getVehicleId()))
                .toList();
    }

    /**
     * SensorReadings se filtran indirectamente via los IDs de sensores del tenant.
     */
    long heartRateReadingCount() {
        Set<Long> tenantSensorIds = sensors().stream()
                .map(Sensor::getId)
                .collect(Collectors.toSet());
        return sensorReadingRepository.findAll().stream()
                .filter(r -> "heart_rate".equalsIgnoreCase(r.getReadingType())
                        && tenantSensorIds.contains(r.getSensorId()))
                .count();
    }

    long usersCount() {
        var id = securityContext.currentCompanyId();
        if (id == null) return 0;
        return userRepository.findAll().stream()
                .filter(u -> id.equals(u.getCompanyId()))
                .count();
    }

    long supervisorsCount() {
        var id = securityContext.currentCompanyId();
        if (id == null) return 0;
        // Supervisor no tiene companyId; se filtra via el User vinculado
        Set<Long> tenantUserIds = userRepository.findAll().stream()
                .filter(u -> id.equals(u.getCompanyId()))
                .map(u -> u.getId())
                .collect(Collectors.toSet());
        return supervisorRepository.findAll().stream()
                .filter(s -> tenantUserIds.contains(s.getUserId()))
                .count();
    }

    long lockedSupervisorsCount() {
        var id = securityContext.currentCompanyId();
        if (id == null) return 0;
        Set<Long> tenantUserIds = userRepository.findAll().stream()
                .filter(u -> id.equals(u.getCompanyId()))
                .map(u -> u.getId())
                .collect(Collectors.toSet());
        return supervisorRepository.findAll().stream()
                .filter(s -> tenantUserIds.contains(s.getUserId()))
                .filter(s -> s.getAccessStatus() != null && "locked".equals(s.getAccessStatus().toSerialized()))
                .count();
    }

    List<PerformanceMetric> metrics() {
        var id = securityContext.currentCompanyId();
        if (id == null) return List.of();
        // PerformanceMetric no tiene companyId; se filtra por driverIds del tenant
        Set<Long> tenantDriverIds = drivers().stream().map(Driver::getId).collect(Collectors.toSet());
        return performanceMetricRepository.findAll().stream()
                .filter(e -> tenantDriverIds.contains(e.getDriverId()))
                .map(this::toMetric)
                .toList();
    }

    // ── Proyecciones de mapas ────────────────────────────────────────────────

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

    // ── Helpers de lookup ───────────────────────────────────────────────────

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
        var trip = tripsById().get(alert.getTripId());
        return trip == null ? Optional.empty() : Optional.ofNullable(vehiclesById().get(trip.getVehicleId()));
    }

    Optional<Driver> driverForAlert(Alert alert) {
        var trip = tripsById().get(alert.getTripId());
        return trip == null ? Optional.empty() : Optional.ofNullable(driversById().get(trip.getDriverId()));
    }

    long activeVehiclesCount() {
        return vehicles().stream().filter(v -> v.getStatus() == VehicleStatus.OPERATIONAL).count();
    }

    // ── Helpers de formato ──────────────────────────────────────────────────

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

    // ── Helpers privados ─────────────────────────────────────────────────────

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
