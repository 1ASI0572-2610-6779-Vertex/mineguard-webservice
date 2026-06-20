package com.mineguard.platform.analytics.application.internal.queryservices;

import com.mineguard.platform.analytics.application.queryservices.AnalyticsIncidentDistributionQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.AnalyticsIncidentDistribution;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class AnalyticsIncidentDistributionQueryServiceImpl implements AnalyticsIncidentDistributionQueryService {
    private final AnalyticsProjectionSupport support;

    public AnalyticsIncidentDistributionQueryServiceImpl(AnalyticsProjectionSupport support) {
        this.support = support;
    }

    @Override
    public List<AnalyticsIncidentDistribution> findAll() {
        Map<Long, com.mineguard.platform.monitoring.domain.model.aggregates.Alert> alertsById = support.alertsById();
        Map<String, Long> counts = support.incidents().stream()
                .map(i -> alertsById.get(i.getAlertId()))
                .filter(a -> a != null)
                .collect(Collectors.groupingBy(support::incidentLabel, LinkedHashMap::new, Collectors.counting()));
        int total = counts.values().stream().mapToInt(Long::intValue).sum();
        var index = new AtomicInteger(1);
        return counts.entrySet().stream().map(e -> {
            int id = index.getAndIncrement();
            var item = new AnalyticsIncidentDistribution(e.getKey(), e.getValue().intValue(),
                    total == 0 ? 0 : (int) Math.round(e.getValue() * 100.0 / total), "color-" + id);
            item.setId((long) id);
            return item;
        }).toList();
    }
}
