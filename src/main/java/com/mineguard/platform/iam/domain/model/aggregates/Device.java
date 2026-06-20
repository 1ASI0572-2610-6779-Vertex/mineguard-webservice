package com.mineguard.platform.iam.domain.model.aggregates;

import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/**
 * Device aggregate root representing a registered smart-band IoT device.
 *
 * <p>Mirrors the IAM concept of the {@code smart-band-edge-service}: a device
 * is identified by a unique {@code deviceId} and authenticated through its
 * paired {@code apiKey} (transmitted via the {@code X-API-Key} header). A device
 * may be linked to the driver wearing it via {@code driverId}, so heart-rate
 * telemetry synchronized from the edge can be attributed to an operator.</p>
 */
@Getter
public class Device extends AbstractDomainAggregateRoot<Device> {

    @Setter
    private Long id;
    @Setter
    private String deviceId;
    @Setter
    private String apiKey;
    @Setter
    private Long driverId;

    public Device() {
    }

    public Device(String deviceId, String apiKey, Long driverId) {
        this.deviceId = deviceId;
        this.apiKey = apiKey;
        this.driverId = driverId;
    }

    /** Returns true when the supplied API key matches this device's credential. */
    public boolean authenticatesWith(String candidateApiKey) {
        return apiKey != null && apiKey.equals(candidateApiKey);
    }
}
