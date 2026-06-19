package com.mineguard.platform.assets.application.internal.queryservices;

import com.mineguard.platform.assets.application.queryservices.CatalogSummaryQueryService;
import com.mineguard.platform.assets.domain.model.aggregates.CatalogSummary;
import com.mineguard.platform.assets.domain.model.queries.GetCatalogSummaryQuery;
import com.mineguard.platform.assets.domain.model.valueobjects.ShiftStatus;
import com.mineguard.platform.assets.domain.model.valueobjects.VehicleStatus;
import com.mineguard.platform.assets.domain.repositories.DriverRepository;
import com.mineguard.platform.assets.domain.repositories.VehicleRepository;
import com.mineguard.platform.iam.domain.repositories.SupervisorRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CatalogSummaryQueryServiceImpl implements CatalogSummaryQueryService {
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final SupervisorRepository supervisorRepository;

    public CatalogSummaryQueryServiceImpl(DriverRepository driverRepository, VehicleRepository vehicleRepository,
                                          SupervisorRepository supervisorRepository) {
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.supervisorRepository = supervisorRepository;
    }

    @Override
    public Optional<CatalogSummary> handle(GetCatalogSummaryQuery query) {
        var supervisors = supervisorRepository.findAll();
        var summary = new CatalogSummary(
                (int) driverRepository.count(),
                (int) driverRepository.countByShiftStatus(ShiftStatus.INACTIVE),
                (int) vehicleRepository.count(),
                (int) vehicleRepository.countByStatus(VehicleStatus.MAINTENANCE),
                supervisors.size(),
                (int) supervisors.stream().filter(s -> s.getAccessStatus() != null && "locked".equals(s.getAccessStatus().toSerialized())).count());
        summary.setId(1L);
        return Optional.of(summary);
    }
}
