package com.mineguard.platform.iam.infrastructure.persistence.jpa.entities;

import com.mineguard.platform.iam.domain.model.valueobjects.AccessStatus;
import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "administrators")
@Getter
@Setter
@NoArgsConstructor
public class AdministratorPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "email", unique = true, length = 120)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_status", length = 20, nullable = false)
    private AccessStatus accessStatus;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
}