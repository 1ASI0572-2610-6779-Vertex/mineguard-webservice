package com.mineguard.platform.monitoring.application.internal.commandservices;

import com.mineguard.platform.monitoring.application.commandservices.AlertCommandService;
import com.mineguard.platform.monitoring.application.internal.AuditLogWriter;
import com.mineguard.platform.monitoring.domain.model.aggregates.Alert;
import com.mineguard.platform.monitoring.domain.model.commands.CreateCardiacAlertCommand;
import com.mineguard.platform.monitoring.domain.model.commands.CreateEmergencySosAlertCommand;
import com.mineguard.platform.monitoring.domain.model.commands.CreateProximityAlertCommand;
import com.mineguard.platform.monitoring.domain.model.commands.UpdateAlertCommand;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertPriority;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertStatus;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertType;
import com.mineguard.platform.monitoring.domain.repositories.AlertRepository;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Service
public class AlertCommandServiceImpl implements AlertCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlertCommandServiceImpl.class);

    /** Below this distance an obstacle is unavoidable, not merely close. */
    private static final int IMMINENT_IMPACT_CM = 10;

    /**
     * How long an open alert absorbs further samples of the same kind from the same device.
     * Telemetry arrives continuously and a condition like tachycardia persists across thousands of
     * samples; without this, every sample would insert its own row.
     */
    private static final Duration ALERT_COOLDOWN = Duration.ofMinutes(5);

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
        var priority = proximityPriority(command);
        var open = openAlertCovering(command.sensorId(), rawType, priority, command.occurredAt());
        if (open.isPresent()) return Result.success(open.get());
        var alert = new Alert(command.tripId(), command.sensorId(), rawType, priority.toSerialized(),
                AlertStatus.ACTIVE, command.occurredAt());
        alert.setType(type);
        alert.setPriority(priority);
        alert.setTitle(command.collision() ? "Collision detected" : "Proximity warning");
        alert.setDescription(description);
        alert.setCompanyId(command.companyId());
        return Result.success(alertRepository.save(alert));
    }

    /**
     * A confirmed impact is always CRITICAL. An obstacle is graded by how little room is left:
     * inside {@link #IMMINENT_IMPACT_CM} the operator can no longer brake in time, so it is CRITICAL;
     * between there and the alert threshold it is a MEDIUM warning.
     */
    private AlertPriority proximityPriority(CreateProximityAlertCommand command) {
        if (command.collision()) return AlertPriority.CRITICAL;
        if (command.distanceCm() != null && command.distanceCm() < IMMINENT_IMPACT_CM) return AlertPriority.CRITICAL;
        return AlertPriority.MEDIUM;
    }

    /**
     * Returns the open alert that already covers this event, if any — in which case the caller must
     * reuse it instead of inserting a duplicate.
     *
     * <p>Suppression is skipped when the incoming event is <em>more severe</em> than the open one:
     * an obstacle closing from 18 cm (MEDIUM) to 5 cm (CRITICAL) is an escalation the supervisor
     * has to see, not a repeat of the same warning.</p>
     *
     * <p>Once a supervisor resolves the alert its status leaves ACTIVE, so a recurrence raises a
     * fresh one immediately, without waiting out the cooldown.</p>
     */
    private Optional<Alert> openAlertCovering(Long sensorId, String rawType, AlertPriority priority, String occurredAt) {
        return alertRepository.findLatestActiveBySensorIdAndRawType(sensorId, rawType)
                .filter(open -> !priority.isMoreSevereThan(open.getPriority()))
                .filter(open -> withinCooldown(open.getOccurredAt(), occurredAt));
    }

    private boolean withinCooldown(String openOccurredAt, String incomingOccurredAt) {
        var open = parseInstant(openOccurredAt);
        var incoming = parseInstant(incomingOccurredAt);
        // An unparsable timestamp must not become a licence to insert one row per sample.
        if (open == null || incoming == null) return true;
        return !open.isBefore(incoming.minus(ALERT_COOLDOWN));
    }

    private Instant parseInstant(String value) {
        if (value == null) return null;
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException e) {
            LOGGER.warn("Unparsable alert timestamp '{}' — treating as within cooldown", value);
            return null;
        }
    }

    @Override
    public Result<Alert, ApplicationError> handle(CreateCardiacAlertCommand command) {
        var condition = command.condition();
        var tripReference = command.tripId() == null ? "without active trip" : "trip " + command.tripId();
        var open = openAlertCovering(command.sensorId(), condition.rawType(), condition.priority(), command.occurredAt());
        if (open.isPresent()) return Result.success(open.get());
        var alert = new Alert(command.tripId(), command.sensorId(), condition.rawType(),
                condition.priority().toSerialized(), AlertStatus.ACTIVE, command.occurredAt());
        alert.setType(AlertType.fromSerialized(condition.rawType()));
        alert.setPriority(condition.priority());
        alert.setTitle(condition.title());
        alert.setDescription(condition.title() + ": " + Math.round(command.bpm()) + " bpm (" + tripReference + ")");
        alert.setCompanyId(command.companyId());
        return Result.success(alertRepository.save(alert));
    }

    @Override
    public Result<Alert, ApplicationError> handle(CreateEmergencySosAlertCommand command) {
        var tripReference = command.tripId() == null ? "without active trip" : "trip " + command.tripId();
        // A held button emits on every sampling tick; the operator pressed it once.
        var open = openAlertCovering(command.sensorId(), "emergency_sos", AlertPriority.CRITICAL, command.occurredAt());
        if (open.isPresent()) return Result.success(open.get());
        var alert = new Alert(command.tripId(), command.sensorId(), "emergency_sos", "critical",
                AlertStatus.ACTIVE, command.occurredAt());
        alert.setType(AlertType.EMERGENCY_SOS);
        alert.setPriority(AlertPriority.CRITICAL);
        alert.setTitle("Emergency SOS triggered");
        alert.setDescription("Operator pressed the emergency button on the device (" + tripReference + ")");
        alert.setCompanyId(command.companyId());
        return Result.success(alertRepository.save(alert));
    }
}
