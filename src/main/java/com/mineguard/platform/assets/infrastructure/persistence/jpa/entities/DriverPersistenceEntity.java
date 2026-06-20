package com.mineguard.platform.assets.infrastructure.persistence.jpa.entities;

import com.mineguard.platform.assets.domain.model.valueobjects.ShiftStatus;
import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
public class DriverPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;
    @Column(name = "operator_id", length = 30)
    private String operatorId;
    @Column(length = 30)
    private String license;
    @Column(length = 60)
    private String specialty;
    @Enumerated(EnumType.STRING)
    @Column(name = "shift_status", length = 20, nullable = false)
    private ShiftStatus shiftStatus;
    @Column(name = "last_access", length = 60)
    private String lastAccess;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "company_id")
    private Long companyId;
}
