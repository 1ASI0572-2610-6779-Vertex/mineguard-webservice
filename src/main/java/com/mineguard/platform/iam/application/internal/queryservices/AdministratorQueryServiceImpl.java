package com.mineguard.platform.iam.application.internal.queryservices;

import com.mineguard.platform.iam.application.queryservices.AdministratorQueryService;
import com.mineguard.platform.iam.domain.model.aggregates.Administrator;
import com.mineguard.platform.iam.domain.model.queries.GetAllAdministratorsQuery;
import com.mineguard.platform.iam.domain.repositories.AdministratorRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdministratorQueryServiceImpl implements AdministratorQueryService {
    private final AdministratorRepository administratorRepository;

    public AdministratorQueryServiceImpl(AdministratorRepository administratorRepository) {
        this.administratorRepository = administratorRepository;
    }

    @Override
    public List<Administrator> handle(GetAllAdministratorsQuery query) {
        return administratorRepository.findAll();
    }
}