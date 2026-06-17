package com.mineguard.platform.analytics.application.internal.queryservices;

import com.mineguard.platform.analytics.application.queryservices.AdminNoticeQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.AdminNotice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminNoticeQueryServiceImpl implements AdminNoticeQueryService {
    private final AnalyticsProjectionSupport support;

    public AdminNoticeQueryServiceImpl(AnalyticsProjectionSupport support) {
        this.support = support;
    }

    @Override
    public List<AdminNotice> findAll() {
        long locked = support.lockedSupervisorsCount();
        var warning = new AdminNotice("warning", "admin.notices.passwordChangePending",
                "{\"count\":" + locked + "}", "admin.notices.action.resendInvite");
        warning.setId(1L);
        var info = new AdminNotice("info", "admin.notices.consolidatedReportReady", "{}",
                "admin.notices.action.downloadPdf");
        info.setId(2L);
        return List.of(warning, info);
    }
}
