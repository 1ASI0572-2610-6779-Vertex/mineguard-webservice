package com.mineguard.platform.iam.infrastructure.persistence.jpa.entities;

import com.mineguard.platform.iam.domain.model.valueobjects.AccessStatus;
import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** JPA persistence entity for supervisor directory accounts. */
@Entity
@Table(name = "supervisors")
@Getter
@Setter
@NoArgsConstructor
public class SupervisorPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "corporate_id", nullable = false, unique = true, length = 30)
    private String corporateId;

    @Column(name = "email", length = 120)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_status", length = 20, nullable = false)
    private AccessStatus accessStatus;
}
