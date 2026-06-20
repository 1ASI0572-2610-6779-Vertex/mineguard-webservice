package com.mineguard.platform.iam.application.internal.queryservices;

import com.mineguard.platform.iam.application.queryservices.SupervisorQueryService;
import com.mineguard.platform.iam.domain.model.aggregates.Supervisor;
import com.mineguard.platform.iam.domain.model.queries.GetAllSupervisorsQuery;
import com.mineguard.platform.iam.domain.model.queries.GetSupervisorByIdQuery;
import com.mineguard.platform.iam.domain.repositories.SupervisorRepository;
import com.mineguard.platform.iam.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupervisorQueryServiceImpl implements SupervisorQueryService {
    private final SupervisorRepository supervisorRepository;
    private final UserRepository userRepository;

    public SupervisorQueryServiceImpl(SupervisorRepository supervisorRepository, UserRepository userRepository) {
        this.supervisorRepository = supervisorRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Supervisor> handle(GetAllSupervisorsQuery query) {
        return supervisorRepository.findAll().stream().map(this::enrichFromUser).toList();
    }

    @Override
    public Optional<Supervisor> handle(GetSupervisorByIdQuery query) {
        return supervisorRepository.findById(query.supervisorId()).map(this::enrichFromUser);
    }

    private Supervisor enrichFromUser(Supervisor supervisor) {
        if (supervisor.getUserId() == null) return supervisor;
        userRepository.findById(supervisor.getUserId()).ifPresent(user -> {
            supervisor.setFullName(user.getFullName());
            supervisor.setEmail(user.getEmail());
        });
        return supervisor;
    }
}
