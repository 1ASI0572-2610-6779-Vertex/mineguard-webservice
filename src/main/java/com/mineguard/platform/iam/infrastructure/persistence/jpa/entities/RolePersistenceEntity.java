package com.mineguard.platform.iam.infrastructure.persistence.jpa.entities;

import com.mineguard.platform.iam.domain.model.valueobjects.Roles;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** JPA persistence entity for IAM roles. */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class RolePersistenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, unique = true, nullable = false)
    private Roles name;

    public RolePersistenceEntity(Roles name) {
        this.name = name;
    }
}
