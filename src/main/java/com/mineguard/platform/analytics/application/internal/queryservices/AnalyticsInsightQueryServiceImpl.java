package com.mineguard.platform.analytics.application.internal.queryservices;

import com.mineguard.platform.analytics.application.queryservices.AnalyticsInsightQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.AnalyticsInsight;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsInsightQueryServiceImpl implements AnalyticsInsightQueryService {
    private final AnalyticsProjectionSupport support;

    public AnalyticsInsightQueryServiceImpl(AnalyticsProjectionSupport support) {
        this.support = support;
    }

    @Override
    public List<AnalyticsInsight> findAll() {
        int fatigueEvents = support.metrics().stream().mapToInt(m -> m.getFatigueEvents()).sum();
        long highIncidents = support.incidents().stream().filter(i -> "high".equalsIgnoreCase(i.getSeverity())).count();
        var fatigue = new AnalyticsInsight("Patron de Fatiga Detectado",
                "Se registraron " + fatigueEvents + " eventos de fatiga acumulados en las metricas de rendimiento. Se recomienda revisar los turnos con mayor riesgo.",
                "fatigue-insight");
        fatigue.setId(1L);
        var zone = new AnalyticsInsight("Zona Critica Identificada",
                "Se registraron " + highIncidents + " incidentes de alta severidad. Se recomienda reforzar el monitoreo en rutas autonomas.",
                "zone-insight");
        zone.setId(2L);
        return List.of(fatigue, zone);
    }
}
