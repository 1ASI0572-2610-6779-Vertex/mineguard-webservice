package com.mineguard.platform.assets.domain.model.aggregates;

import com.mineguard.platform.assets.domain.model.valueobjects.VehicleStatus;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/**
 * Vehicle aggregate root. Carries the attributes consumed by the web fleet
 * inventory ({@code vehiclesInventory}) and the mobile vehicle selection view.
 */
@Getter
public class Vehicle extends AbstractDomainAggregateRoot<Vehicle> {

    @Setter private Long id;
    @Setter private String code;
    @Setter private String model;
    @Setter private String category;
    @Setter private VehicleStatus status;
    @Setter private String assignedDriverName;
    @Setter private String shiftLabel;
    @Setter private Long driverId;
    @Setter private String vehicleType;
    @Setter private Long companyId;
    /** Soft-delete flag: an archived (decommissioned) vehicle is hidden from the inventory by default. */
    @Setter private boolean archived;

    public Vehicle() {
    }

    public Vehicle(String code, String model, String category, VehicleStatus status,
                   String assignedDriverName, String shiftLabel) {
        this.code = code;
        this.model = model;
        this.category = category;
        this.status = status;
        this.assignedDriverName = assignedDriverName;
        this.shiftLabel = shiftLabel;
    }

    public Vehicle(Long driverId, String code, String vehicleType, VehicleStatus status) {
        this.driverId = driverId;
        this.code = code;
        this.vehicleType = vehicleType;
        this.model = vehicleType;
        this.category = vehicleType;
        this.status = status;
    }

    /** Partial update (PATCH semantics): every parameter is optional — {@code null} leaves the current value unchanged. */
    public Vehicle updateInformation(String code, String model, String category, VehicleStatus status,
                                     String assignedDriverName, String shiftLabel) {
        if (code != null) this.code = code;
        if (model != null) this.model = model;
        if (category != null) this.category = category;
        if (status != null) this.status = status;
        if (assignedDriverName != null) this.assignedDriverName = assignedDriverName;
        if (shiftLabel != null) this.shiftLabel = shiftLabel;
        return this;
    }

    public boolean isAssigned() {
        return assignedDriverName != null && !assignedDriverName.isBlank();
    }

    /** Archive (decommission) this vehicle without deleting its row — preserves history. */
    public void archive() {
        this.archived = true;
    }
}
