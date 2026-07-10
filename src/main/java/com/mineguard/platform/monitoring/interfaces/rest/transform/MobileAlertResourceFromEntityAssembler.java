package com.mineguard.platform.monitoring.interfaces.rest.transform;

import com.mineguard.platform.monitoring.domain.model.aggregates.Alert;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertType;
import com.mineguard.platform.monitoring.interfaces.rest.resources.MobileAlertResource;

/** Projects an operational Alert onto the mobile safety-alert contract. */
public final class MobileAlertResourceFromEntityAssembler {
    private MobileAlertResourceFromEntityAssembler() {
    }

    public static MobileAlertResource toResourceFromEntity(Alert a) {
        String kind = switch (a.getType()) {
            case FATIGUE -> "fatigue";
            case COLLISION, IMMINENT_COLLISION, PROXIMITY_COLLISION, PROXIMITY -> "collisionRisk";
            case HIGH_HEART_RATE, CARDIAC_ARREST -> "cardiac";
            case EMERGENCY_SOS -> "sos";
        };
        // A fatigue alert is the only one the operator can dismiss from the phone: the others
        // describe a hazard the driver cannot clear by acknowledging it.
        String primaryAction = a.getType() == AlertType.FATIGUE ? "Marcar como Revisado" : null;
        return new MobileAlertResource(String.valueOf(a.getId()), kind, a.getTitle(), a.getDescription(),
                a.getOccurredAt(), primaryAction);
    }
}
