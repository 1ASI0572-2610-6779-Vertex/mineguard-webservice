package com.mineguard.platform.analytics.application.queryservices;

import com.mineguard.platform.analytics.domain.model.aggregates.AnalyticsHistoryRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnalyticsHistoryRowQueryService {
    /**
     * Returns a page of history rows for the given company, or an empty page if {@code companyId}
     * does not match the authenticated caller's own tenant (ownership is enforced here, not by the
     * caller). Pagination is mandatory — this collection grows without bound over the life of a
     * tenant, so an unpaged "return everything" call would risk an OutOfMemoryError on both the
     * server (building the response) and the client (rendering it).
     */
    Page<AnalyticsHistoryRow> findForCompany(Long companyId, Pageable pageable);
}
