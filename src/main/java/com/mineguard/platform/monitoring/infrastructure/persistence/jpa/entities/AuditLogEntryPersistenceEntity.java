package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities;

import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audit_log_entries")
@Getter
@Setter
@NoArgsConstructor
public class AuditLogEntryPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(length = 30)
    private String category;
    @Column(name = "occurred_at", length = 40)
    private String occurredAt;
    @Column(name = "title_key", length = 160)
    private String titleKey;
    @Column(name = "description_key", length = 200)
    private String descriptionKey;
    @Column(name = "description_params_json", length = 500)
    private String descriptionParamsJson;
    @Column(name = "actor_key", length = 160)
    private String actorKey;
}
