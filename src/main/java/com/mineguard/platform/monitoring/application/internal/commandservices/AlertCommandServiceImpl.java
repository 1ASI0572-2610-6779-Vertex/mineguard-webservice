package com.mineguard.platform.monitoring.application.internal.commandservices;

import com.mineguard.platform.monitoring.application.commandservices.AlertCommandService;
import com.mineguard.platform.monitoring.domain.model.aggregates.Alert;
import com.mineguard.platform.monitoring.domain.model.commands.MarkAlertActionCommand;
import com.mineguard.platform.monitoring.domain.model.commands.UpdateAlertCommand;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertStatus;
import com.mineguard.platform.monitoring.domain.repositories.AlertRepository;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class AlertCommandServiceImpl implements AlertCommandService {
    private final AlertRepository alertRepository;

    public AlertCommandServiceImpl(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
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
        // Both "mark reviewed" and "call cabin" resolve the alert in the mobile flow.
        alert.classify(AlertStatus.RESOLVED, "Handled via mobile action: " + command.action());
        return Result.success(alertRepository.save(alert));
    }
}
