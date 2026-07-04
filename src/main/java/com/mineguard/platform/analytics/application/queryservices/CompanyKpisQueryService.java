package com.mineguard.platform.analytics.application.queryservices;

import com.mineguard.platform.analytics.domain.model.aggregates.CompanyKpis;

import java.util.Optional;

public interface CompanyKpisQueryService {
    /**
     * Returns the KPIs for the given company, or empty if {@code companyId} does not match
     * the authenticated caller's own tenant (ownership is enforced here, not by the caller).
     */
    Optional<CompanyKpis> findForCompany(Long companyId);
}
