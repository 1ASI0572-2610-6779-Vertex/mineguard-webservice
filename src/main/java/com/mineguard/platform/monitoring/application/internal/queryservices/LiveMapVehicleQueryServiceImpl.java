package com.mineguard.platform.monitoring.application.internal.queryservices;

import com.mineguard.platform.assets.domain.repositories.VehicleRepository;
import com.mineguard.platform.monitoring.application.queryservices.LiveMapVehicleQueryService;
import com.mineguard.platform.monitoring.domain.model.aggregates.LiveMapVehicle;
import com.mineguard.platform.monitoring.domain.model.queries.GetAllLiveMapVehiclesQuery;
import com.mineguard.platform.monitoring.domain.repositories.LiveMapVehicleRepository;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LiveMapVehicleQueryServiceImpl implements LiveMapVehicleQueryService {

    private final LiveMapVehicleRepository repository;
    private final VehicleRepository vehicleRepository;
    private final SecurityContextFacade securityContext;

    public LiveMapVehicleQueryServiceImpl(LiveMapVehicleRepository repository,
                                          VehicleRepository vehicleRepository,
                                          SecurityContextFacade securityContext) {
        this.repository = repository;
        this.vehicleRepository = vehicleRepository;
        this.securityContext = securityContext;
    }

    @Override
    public List<LiveMapVehicle> handle(GetAllLiveMapVehiclesQuery query) {
        var companyId = securityContext.currentCompanyId();
        if (companyId == null) return List.of();
        // LiveMapVehicle no tiene companyId; se filtra por los códigos de vehículos del tenant
        Set<String> tenantCodes = vehicleRepository.findAllByCompanyId(companyId).stream()
                .map(v -> v.getCode())
                .collect(Collectors.toSet());
        return repository.findAll().stream()
                .filter(lmv -> tenantCodes.contains(lmv.getCode()))
                .toList();
    }
}
