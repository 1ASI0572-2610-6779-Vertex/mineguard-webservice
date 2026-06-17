package com.mineguard.platform.analytics.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class AnalyticsHistoryRow extends AbstractDomainAggregateRoot<AnalyticsHistoryRow> {
    @Setter private Long id; @Setter private String date; @Setter private String time; @Setter private String criticality;
    @Setter private String criticalityLabel; @Setter private String incidentType; @Setter private String involved; @Setter private String location;
    public AnalyticsHistoryRow() {}
    public AnalyticsHistoryRow(String date,String time,String criticality,String criticalityLabel,String incidentType,String involved,String location){
        this.date=date;this.time=time;this.criticality=criticality;this.criticalityLabel=criticalityLabel;this.incidentType=incidentType;this.involved=involved;this.location=location;}
}
