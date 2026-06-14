package com.mineguard.platform.iam.application.internal.queryservices;

import com.mineguard.platform.iam.application.queryservices.SupervisorQueryService;
import com.mineguard.platform.iam.domain.model.aggregates.Supervisor;
import com.mineguard.platform.iam.domain.model.queries.GetAllSupervisorsQuery;
import com.mineguard.platform.iam.domain.model.queries.GetSupervisorByIdQuery;
import com.mineguard.platform.iam.domain.repositories.SupervisorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupervisorQueryServiceImpl implements SupervisorQueryService {
    private final SupervisorRepository supervisorRepository;

    public SupervisorQueryServiceImpl(SupervisorRepository supervisorRepository) {
        this.supervisorRepository = supervisorRepository;
    }

    @Override
    public List<Supervisor> handle(GetAllSupervisorsQuery query) {
        return supervisorRepository.findAll();
    }

    @Override
    public Optional<Supervisor> handle(GetSupervisorByIdQuery query) {
        return supervisorRepository.findById(query.supervisorId());
    }
}
