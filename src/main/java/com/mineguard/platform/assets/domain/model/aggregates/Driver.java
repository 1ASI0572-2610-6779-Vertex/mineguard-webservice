package com.mineguard.platform.assets.domain.model.aggregates;

import com.mineguard.platform.assets.domain.model.valueobjects.ShiftStatus;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/**
 * Driver aggregate root. Carries the attributes consumed by the web drivers
 * directory ({@code driversDirectory}).
 */
@Getter
public class Driver extends AbstractDomainAggregateRoot<Driver> {

    @Setter private Long id;
    @Setter private String fullName;
    @Setter private String operatorId;
    @Setter private String license;
    @Setter private String specialty;
    @Setter private ShiftStatus shiftStatus;
    @Setter private String lastAccess;
    @Setter private Long userId;
    @Setter private Long companyId;

    public Driver() {
    }

    public Driver(String fullName, String operatorId, String license, String specialty,
                  ShiftStatus shiftStatus, String lastAccess, Long userId) {
        this.fullName = fullName;
        this.operatorId = operatorId;
        this.license = license;
        this.specialty = specialty;
        this.shiftStatus = shiftStatus;
        this.lastAccess = lastAccess;
        this.userId = userId;
    }
}
