package com.mineguard.platform.analytics.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class Report extends AbstractDomainAggregateRoot<Report> {
    @Setter private Long id; @Setter private Long incidentId; @Setter private Long alertId; @Setter private Long userId; @Setter private Long metricId;
    @Setter private String reportType; @Setter private String createdAt; @Setter private String description;
    public Report() {}
    public Report(Long incidentId,Long alertId,Long userId,Long metricId,String reportType,String createdAt,String description){
        this.incidentId=incidentId;this.alertId=alertId;this.userId=userId;this.metricId=metricId;this.reportType=reportType;this.createdAt=createdAt;this.description=description;}
}
