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

    /** Updates the editable inventory fields. */
    public Vehicle updateInformation(String code, String model, String category, VehicleStatus status,
                                     String assignedDriverName, String shiftLabel) {
        this.code = code;
        this.model = model;
        this.category = category;
        if (status != null) this.status = status;
        this.assignedDriverName = assignedDriverName;
        this.shiftLabel = shiftLabel;
        return this;
    }

    public boolean isAssigned() {
        return assignedDriverName != null && !assignedDriverName.isBlank();
    }
}
