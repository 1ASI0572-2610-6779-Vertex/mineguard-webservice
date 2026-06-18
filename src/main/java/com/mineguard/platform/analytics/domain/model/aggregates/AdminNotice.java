package com.mineguard.platform.analytics.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class AdminNotice extends AbstractDomainAggregateRoot<AdminNotice> {
    @Setter private Long id; @Setter private String level; @Setter private String i18nKey; @Setter private String i18nParamsJson; @Setter private String actionKey;
    public AdminNotice() {}
    public AdminNotice(String level,String i18nKey,String i18nParamsJson,String actionKey){this.level=level;this.i18nKey=i18nKey;this.i18nParamsJson=i18nParamsJson;this.actionKey=actionKey;}
}
