package com.mineguard.platform.monitoring.application.internal.commandservices;

import com.mineguard.platform.monitoring.application.commandservices.AlertCommandService;
import com.mineguard.platform.monitoring.application.internal.AuditLogWriter;
import com.mineguard.platform.monitoring.domain.model.aggregates.Alert;
import com.mineguard.platform.monitoring.domain.model.commands.CreateProximityAlertCommand;
import com.mineguard.platform.monitoring.domain.model.commands.MarkAlertActionCommand;
import com.mineguard.platform.monitoring.domain.model.commands.UpdateAlertCommand;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertPriority;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertStatus;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertType;
import com.mineguard.platform.monitoring.domain.repositories.AlertRepository;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class AlertCommandServiceImpl implements AlertCommandService {
    private final AlertRepository alertRepository;
    private final AuditLogWriter auditLogWriter;

    public AlertCommandServiceImpl(AlertRepository alertRepository, AuditLogWriter auditLogWriter) {
        this.alertRepository = alertRepository;
        this.auditLogWriter = auditLogWriter;
    }

    @Override
    public Result<Alert, ApplicationError> handle(UpdateAlertCommand command) {
        var existing = alertRepository.findById(command.id());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Alert", String.valueOf(command.id())));
        }
        var alert = existing.get();
        alert.updateAll(command.type(), command.priority(), command.status(), command.title(),
                command.description(), command.vehicleClassKey(), command.vehicleCode(),
                command.driverName(), command.resolutionNotes());
        return Result.success(alertRepository.save(alert));
    }

    @Override
    public Result<Alert, ApplicationError> handle(MarkAlertActionCommand command) {
        var existing = alertRepository.findById(command.alertId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Alert", String.valueOf(command.alertId())));
        }
        var alert = existing.get();
        var action = command.action() == null ? "markReviewed" : command.action();
        switch (action) {
            case "falseAlarm" -> alert.classify(AlertStatus.FALSE_ALARM, "False alarm confirmed");
            case "contactOperator" -> alert.classify(AlertStatus.ACTIVE, "Operator contacted");
            case "resolve", "markReviewed" -> alert.classify(AlertStatus.RESOLVED, "Handled via action: " + action);
            default -> {
                return Result.failure(ApplicationError.validationError("action", "Unsupported alert action: " + action));
            }
        }
        var saved = alertRepository.save(alert);
        auditLogWriter.record("operational", "monitoring.audit.entries.alertAction.title",
                "monitoring.audit.entries.alertAction.description",
                "{\"alertId\":" + command.alertId() + ",\"action\":\"" + action + "\",\"performedBy\":\"Juan Perez\"}",
                "Juan Perez");
        return Result.success(saved);
    }

    @Override
    public Result<Alert, ApplicationError> handle(CreateProximityAlertCommand command) {
        var type = command.collision() ? AlertType.PROXIMITY_COLLISION : AlertType.PROXIMITY;
        var rawType = command.collision() ? "proximity_collision" : "proximity";
        var description = command.collision()
                ? "Collision event reported by sensor (trip " + command.tripId() + ")"
                : "Obstacle detected at " + command.distanceCm() + " cm — below safety threshold (trip " + command.tripId() + ")";
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
