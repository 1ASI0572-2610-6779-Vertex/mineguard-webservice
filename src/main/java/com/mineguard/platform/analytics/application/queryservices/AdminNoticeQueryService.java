package com.mineguard.platform.analytics.application.queryservices;

import com.mineguard.platform.analytics.domain.model.aggregates.AdminNotice;

import java.util.List;

public interface AdminNoticeQueryService {
    List<AdminNotice> findAll();
}
