package com.mineguard.platform.assets.application.internal.queryservices;

import com.mineguard.platform.assets.application.queryservices.VehicleQueryService;
import com.mineguard.platform.assets.domain.model.aggregates.Vehicle;
import com.mineguard.platform.assets.domain.model.queries.GetAllVehiclesQuery;
import com.mineguard.platform.assets.domain.model.queries.GetVehicleByIdQuery;
import com.mineguard.platform.assets.domain.repositories.VehicleRepository;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleQueryServiceImpl implements VehicleQueryService {
    private final VehicleRepository vehicleRepository;
    private final SecurityContextFacade securityContext;

    public VehicleQueryServiceImpl(VehicleRepository vehicleRepository, SecurityContextFacade securityContext) {
        this.vehicleRepository = vehicleRepository;
        this.securityContext = securityContext;
    }

    @Override
    public List<Vehicle> handle(GetAllVehiclesQuery query) {
        var companyId = securityContext.currentCompanyId();
        return companyId != null
                ? vehicleRepository.findAllByCompanyId(companyId)
                : vehicleRepository.findAll();
    }

    @Override
    public Optional<Vehicle> handle(GetVehicleByIdQuery query) {
        return vehicleRepository.findById(query.vehicleId());
    }
}
