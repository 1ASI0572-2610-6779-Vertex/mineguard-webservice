package com.mineguard.platform.monitoring.domain.model.aggregates;

import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/** Read-model backing the audit log (i18n-keyed entries). */
@Getter
public class AuditLogEntry extends AbstractDomainAggregateRoot<AuditLogEntry> {
    @Setter private Long id;
    @Setter private String category;
    @Setter private String occurredAt;
    @Setter private String titleKey;
    @Setter private String descriptionKey;
    @Setter private String descriptionParamsJson;
    @Setter private String actorKey;
    /** Owning tenant, resolved automatically from the active security context at write time. */
    @Setter private Long companyId;

    public AuditLogEntry() {
    }

    public AuditLogEntry(String category, String occurredAt, String titleKey, String descriptionKey,
                         String descriptionParamsJson, String actorKey, Long companyId) {
        this.category = category;
        this.occurredAt = occurredAt;
        this.titleKey = titleKey;
        this.descriptionKey = descriptionKey;
        this.descriptionParamsJson = descriptionParamsJson;
        this.actorKey = actorKey;
        this.companyId = companyId;
    }
}
