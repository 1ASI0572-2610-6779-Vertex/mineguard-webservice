package com.mineguard.platform.monitoring.application.internal.queryservices;

import com.mineguard.platform.monitoring.application.queryservices.AlertQueryService;
import com.mineguard.platform.assets.domain.model.aggregates.Trip;
import com.mineguard.platform.assets.domain.repositories.DriverRepository;
import com.mineguard.platform.assets.domain.repositories.TripRepository;
import com.mineguard.platform.assets.domain.repositories.VehicleRepository;
import com.mineguard.platform.monitoring.domain.model.aggregates.Alert;
import com.mineguard.platform.monitoring.domain.model.queries.GetAlertByIdQuery;
import com.mineguard.platform.monitoring.domain.model.queries.GetAllAlertsQuery;
import com.mineguard.platform.monitoring.domain.repositories.AlertRepository;
import com.mineguard.platform.monitoring.domain.repositories.IncidentRepository;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AlertQueryServiceImpl implements AlertQueryService {
    private final AlertRepository alertRepository;
    private final IncidentRepository incidentRepository;
    private final TripRepository tripRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final SecurityContextFacade securityContext;

    public AlertQueryServiceImpl(AlertRepository alertRepository, IncidentRepository incidentRepository,
                                 TripRepository tripRepository, DriverRepository driverRepository,
                                 VehicleRepository vehicleRepository, SecurityContextFacade securityContext) {
        this.alertRepository = alertRepository;
        this.incidentRepository = incidentRepository;
        this.tripRepository = tripRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.securityContext = securityContext;
    }

    @Override
    public List<Alert> handle(GetAllAlertsQuery query) {
        var companyId = securityContext.currentCompanyId();
        if (companyId == null) return List.of();
        return alertRepository.findAllByCompanyId(companyId).stream().map(this::enrich).toList();
    }

    @Override
    public Optional<Alert> handle(GetAlertByIdQuery query) {
        var companyId = securityContext.currentCompanyId();
        return alertRepository.findById(query.alertId())
                .filter(a -> companyId != null && companyId.equals(a.getCompanyId()))
                .map(this::enrich);
    }

    private Alert enrich(Alert alert) {
        if (alert == null) return null;
        if (alert.getCode() == null || alert.getCode().isBlank()) alert.setCode("AL-" + String.format("%03d", alert.getId()));

        var companyId = securityContext.currentCompanyId();
        Map<Long, Trip> trips = (companyId != null
                ? tripRepository.findAllByCompanyId(companyId)
                : List.<Trip>of()).stream()
                .collect(Collectors.toMap(Trip::getId, Function.identity(), (a, b) -> a));
        var drivers = (companyId != null
                ? driverRepository.findAllByCompanyId(companyId)
                : List.<com.mineguard.platform.assets.domain.model.aggregates.Driver>of()).stream()
                .collect(Collectors.toMap(d -> d.getId(), d -> d, (a, b) -> a));
        var vehicles = (companyId != null
                ? vehicleRepository.findAllByCompanyId(companyId)
                : List.<com.mineguard.platform.assets.domain.model.aggregates.Vehicle>of()).stream()
                .collect(Collectors.toMap(v -> v.getId(), v -> v, (a, b) -> a));
        var trip = trips.get(alert.getTripId());
        if (trip != null) {
            var driver = drivers.get(trip.getDriverId());
            var vehicle = vehicles.get(trip.getVehicleId());
            if (driver != null && (alert.getDriverName() == null || alert.getDriverName().isBlank())) {
                alert.setDriverName(driver.getFullName());
            }
            if (vehicle != null) {
                if (alert.getVehicleCode() == null || alert.getVehicleCode().isBlank()) alert.setVehicleCode(vehicle.getCode());
                if (alert.getVehicleClassKey() == null || alert.getVehicleClassKey().isBlank()) {
                    alert.setVehicleClassKey(vehicle.getVehicleType());
                }
            }
        }
        var incident = (companyId != null ? incidentRepository.findAllByCompanyId(companyId) : List.<com.mineguard.platform.monitoring.domain.model.aggregates.Incident>of()).stream()
                .filter(i -> alert.getId().equals(i.getAlertId()))
                .findFirst();
        if (incident.isPresent() && (alert.getDescription() == null || alert.getDescription().isBlank())) {
            alert.setDescription(incident.get().getDescription());
        }
        if (alert.getTitle() == null || alert.getTitle().isBlank()) alert.setTitle(titleFor(alert));
        return alert;
    }

    private String titleFor(Alert alert) {
        var type = alert.getRawType() == null ? "" : alert.getRawType();
        return switch (type) {
            case "proximity_collision" -> "Proximity Collision";
            case "restricted_zone_entry" -> "Restricted Zone Entry";
            case "high_heart_rate" -> "High Heart Rate";
            case "fatigue_risk" -> "Fatigue Risk";
            case "connection_lost" -> "Connection Lost";
            default -> type.isBlank() ? "Operational Alert" : type.replace('_', ' ');
        };
    }
}
