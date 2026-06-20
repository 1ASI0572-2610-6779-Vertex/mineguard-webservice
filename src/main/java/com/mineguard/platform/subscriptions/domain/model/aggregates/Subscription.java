package com.mineguard.platform.subscriptions.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class Subscription extends AbstractDomainAggregateRoot<Subscription> {
    @Setter private Long id; @Setter private Long companyId; @Setter private Long planId;
    @Setter private String startDate; @Setter private String endDate; @Setter private String status;
    public Subscription() {}
    public Subscription(Long companyId,Long planId,String startDate,String endDate,String status){
        this.companyId=companyId;this.planId=planId;this.startDate=startDate;this.endDate=endDate;this.status=status;}
}
