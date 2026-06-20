package com.mineguard.platform.monitoring.application.internal.queryservices;

import com.mineguard.platform.assets.domain.model.valueobjects.VehicleStatus;
import com.mineguard.platform.assets.domain.repositories.VehicleRepository;
import com.mineguard.platform.monitoring.application.queryservices.FleetSummaryQueryService;
import com.mineguard.platform.monitoring.domain.model.aggregates.FleetSummary;
import com.mineguard.platform.monitoring.domain.model.queries.GetFleetSummaryQuery;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FleetSummaryQueryServiceImpl implements FleetSummaryQueryService {
    private final VehicleRepository vehicleRepository;

    public FleetSummaryQueryServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public Optional<FleetSummary> handle(GetFleetSummaryQuery query) {
        int operational = (int) vehicleRepository.countByStatus(VehicleStatus.OPERATIONAL);
        int maintenance = (int) vehicleRepository.countByStatus(VehicleStatus.MAINTENANCE);
        int alert = (int) (vehicleRepository.countByStatus(VehicleStatus.ALERT)
                + vehicleRepository.countByStatus(VehicleStatus.INACTIVE)
                + vehicleRepository.countByStatus(VehicleStatus.RESTRICTED_ROUTE));
        int total = (int) vehicleRepository.count();
        var summary = new FleetSummary(operational, maintenance, alert, total,
                total == 0 ? 0 : (int) Math.round(operational * 100.0 / total));
        summary.setId(1L);
        return Optional.of(summary);
    }
}
