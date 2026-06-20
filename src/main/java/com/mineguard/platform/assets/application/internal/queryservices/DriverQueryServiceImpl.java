package com.mineguard.platform.assets.application.internal.queryservices;

import com.mineguard.platform.assets.application.queryservices.DriverQueryService;
import com.mineguard.platform.assets.domain.model.aggregates.Driver;
import com.mineguard.platform.assets.domain.model.queries.GetAllDriversQuery;
import com.mineguard.platform.assets.domain.model.queries.GetDriverByIdQuery;
import com.mineguard.platform.assets.domain.model.queries.GetDriverByUserIdQuery;
import com.mineguard.platform.assets.domain.repositories.DriverRepository;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DriverQueryServiceImpl implements DriverQueryService {
    private final DriverRepository driverRepository;
    private final SecurityContextFacade securityContext;

    public DriverQueryServiceImpl(DriverRepository driverRepository, SecurityContextFacade securityContext) {
        this.driverRepository = driverRepository;
        this.securityContext = securityContext;
    }

    @Override
    public List<Driver> handle(GetAllDriversQuery query) {
        var companyId = securityContext.currentCompanyId();
        if (companyId == null) return List.of();
        return driverRepository.findAllByCompanyId(companyId);
    }

    @Override
    public Optional<Driver> handle(GetDriverByIdQuery query) {
        var companyId = securityContext.currentCompanyId();
        return driverRepository.findById(query.id())
                .filter(d -> companyId != null && companyId.equals(d.getCompanyId()));
    }

    @Override
    public Optional<Driver> handle(GetDriverByUserIdQuery query) {
        return driverRepository.findByUserId(query.userId());
    }
}
