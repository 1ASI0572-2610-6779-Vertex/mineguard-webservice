package com.mineguard.platform.analytics.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class AdminSummary extends AbstractDomainAggregateRoot<AdminSummary> {
    @Setter private Long id; @Setter private int activeSensors; @Setter private int totalSensors; @Setter private int lockedAccounts; @Setter private int registeredAssets;
    public AdminSummary() {}
    public AdminSummary(int activeSensors,int totalSensors,int lockedAccounts,int registeredAssets){this.activeSensors=activeSensors;this.totalSensors=totalSensors;this.lockedAccounts=lockedAccounts;this.registeredAssets=registeredAssets;}
}
