package com.mineguard.platform.monitoring.application.internal.commandservices;

import com.mineguard.platform.monitoring.application.commandservices.AlertCommandService;
import com.mineguard.platform.monitoring.application.internal.AuditLogWriter;
import com.mineguard.platform.monitoring.domain.model.aggregates.Alert;
import com.mineguard.platform.monitoring.domain.model.commands.CreateProximityAlertCommand;
import com.mineguard.platform.monitoring.domain.model.commands.UpdateAlertCommand;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertPriority;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertStatus;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertType;
import com.mineguard.platform.monitoring.domain.repositories.AlertRepository;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.stereotype.Service;

@Service
public class AlertCommandServiceImpl implements AlertCommandService {
    private final AlertRepository alertRepository;
    private final AuditLogWriter auditLogWriter;
    private final SecurityContextFacade securityContext;

    public AlertCommandServiceImpl(AlertRepository alertRepository, AuditLogWriter auditLogWriter,
                                   SecurityContextFacade securityContext) {
        this.alertRepository = alertRepository;
        this.auditLogWriter = auditLogWriter;
        this.securityContext = securityContext;
    }

    @Override
    public Result<Alert, ApplicationError> handle(UpdateAlertCommand command) {
        var existing = alertRepository.findById(command.id());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Alert", String.valueOf(command.id())));
        }
        var alert = existing.get();
        var callerCompanyId = securityContext.currentCompanyId();
        if (callerCompanyId == null || !callerCompanyId.equals(alert.getCompanyId())) {
            return Result.failure(ApplicationError.notFound("Alert", String.valueOf(command.id())));
        }
        var previousStatus = alert.getStatus();
        alert.updateAll(command.type(), command.priority(), command.status(), command.title(),
                command.description(), command.vehicleClassKey(), command.vehicleCode(),
                command.driverName(), command.resolutionNotes());
        var saved = alertRepository.save(alert);
        if (command.status() != null && command.status() != previousStatus) {
            var performedBy = securityContext.currentUsername() == null ? "system" : securityContext.currentUsername();
            auditLogWriter.record("operational", "monitoring.audit.entries.alertAction.title",
                    "monitoring.audit.entries.alertAction.description",
                    "{\"alertId\":" + command.id() + ",\"action\":\"" + command.status().toSerialized() +
                            "\",\"performedBy\":\"" + performedBy + "\"}",
                    performedBy);
        }
        return Result.success(saved);
    }

    @Override
    public Result<Alert, ApplicationError> handle(CreateProximityAlertCommand command) {
        var type = command.collision() ? AlertType.PROXIMITY_COLLISION : AlertType.PROXIMITY;
        var rawType = command.collision() ? "proximity_collision" : "proximity";
        var tripReference = command.tripId() == null ? "without active trip" : "trip " + command.tripId();
        var description = command.collision()
                ? "Collision event reported by sensor (" + tripReference + ")"
                : "Obstacle detected at " + command.distanceCm() + " cm — below safety threshold (" + tripReference + ")";
        var alert = new Alert(command.tripId(), command.sensorId(), rawType, "critical",
                AlertStatus.ACTIVE, command.occurredAt());
        alert.setType(type);
        alert.setPriority(AlertPriority.CRITICAL);
        alert.setTitle(command.collision() ? "Collision detected" : "Proximity warning");
        alert.setDescription(description);
        alert.setCompanyId(command.companyId());
        return Result.success(alertRepository.save(alert));
    }
}
