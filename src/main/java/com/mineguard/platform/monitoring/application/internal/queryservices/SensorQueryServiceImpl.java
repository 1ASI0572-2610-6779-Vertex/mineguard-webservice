package com.mineguard.platform.monitoring.application.internal.queryservices;

import com.mineguard.platform.monitoring.application.queryservices.SensorQueryService;
import com.mineguard.platform.monitoring.domain.model.aggregates.Sensor;
import com.mineguard.platform.monitoring.domain.repositories.SensorRepository;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SensorQueryServiceImpl implements SensorQueryService {

    private final SensorRepository sensorRepository;
    private final SecurityContextFacade securityContext;

    public SensorQueryServiceImpl(SensorRepository sensorRepository, SecurityContextFacade securityContext) {
        this.sensorRepository = sensorRepository;
        this.securityContext = securityContext;
    }

    @Override
    public List<Sensor> findAllForCurrentCompany() {
        var companyId = securityContext.currentCompanyId();
        if (companyId == null) return List.of();
        return sensorRepository.findAllByCompanyId(companyId);
    }

    @Override
    public Optional<Sensor> findActiveByVehicleForCurrentCompany(Long vehicleId) {
        var companyId = securityContext.currentCompanyId();
        if (companyId == null) return Optional.empty();
        return sensorRepository.findActiveByVehicleIdAndCompanyId(vehicleId, companyId);
    }
}