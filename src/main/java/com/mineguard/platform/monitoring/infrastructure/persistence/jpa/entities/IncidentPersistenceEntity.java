package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities;

import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "incidents")
@Getter
@Setter
@NoArgsConstructor
public class IncidentPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "alert_id")
    private Long alertId;
    @Column(length = 500)
    private String description;
    @Column(name = "incident_date", length = 40)
    private String incidentDate;
    @Column(length = 20)
    private String status;
    @Column(length = 20)
    private String severity;
}
