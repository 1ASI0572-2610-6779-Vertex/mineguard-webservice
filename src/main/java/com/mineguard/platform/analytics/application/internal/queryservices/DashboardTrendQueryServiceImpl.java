package com.mineguard.platform.analytics.application.internal.queryservices;

import com.mineguard.platform.analytics.application.queryservices.DashboardTrendQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.DashboardTrend;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class DashboardTrendQueryServiceImpl implements DashboardTrendQueryService {
    private final AnalyticsProjectionSupport support;

    public DashboardTrendQueryServiceImpl(AnalyticsProjectionSupport support) {
        this.support = support;
    }

    @Override
    public List<DashboardTrend> findAll() {
        Map<String, Long> alertsByHour = support.alerts().stream()
                .collect(Collectors.groupingBy(a -> support.hour(a.getOccurredAt()), Collectors.counting()));
        Map<String, Long> incidentsByHour = support.incidents().stream()
                .collect(Collectors.groupingBy(i -> support.hour(i.getIncidentDate()), Collectors.counting()));
        var hours = new TreeSet<String>();
        hours.addAll(alertsByHour.keySet());
        hours.addAll(incidentsByHour.keySet());
        return hours.stream().filter(h -> !h.isBlank()).map(h -> {
            var trend = new DashboardTrend(h, alertsByHour.getOrDefault(h, 0L).intValue(),
                    incidentsByHour.getOrDefault(h, 0L).intValue());
            trend.setId((long) (hours.headSet(h).size() + 1));
            return trend;
        }).toList();
    }
}
