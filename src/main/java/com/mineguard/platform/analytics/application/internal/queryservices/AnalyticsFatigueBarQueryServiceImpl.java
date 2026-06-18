package com.mineguard.platform.analytics.application.internal.queryservices;

import com.mineguard.platform.analytics.application.queryservices.AnalyticsFatigueBarQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.AnalyticsFatigueBar;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsFatigueBarQueryServiceImpl implements AnalyticsFatigueBarQueryService {
    private final AnalyticsProjectionSupport support;

    public AnalyticsFatigueBarQueryServiceImpl(AnalyticsProjectionSupport support) {
        this.support = support;
    }

    @Override
    public List<AnalyticsFatigueBar> findAll() {
        Map<Long, Integer> fatigueByDriver = support.metrics().stream()
                .collect(Collectors.groupingBy(m -> m.getDriverId(), Collectors.summingInt(m -> m.getFatigueEvents())));
        int max = fatigueByDriver.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        return support.drivers().stream()
                .map(d -> {
                    int events = fatigueByDriver.getOrDefault(d.getId(), 0);
                    int width = max == 0 ? 0 : (int) Math.round(events * 100.0 / max);
                    var item = new AnalyticsFatigueBar(d.getId(), d.getFullName(), events, width);
                    item.setId(d.getId());
                    return item;
                })
                .sorted(Comparator.comparingInt(AnalyticsFatigueBar::getFatigueEvents).reversed())
                .toList();
    }
}
