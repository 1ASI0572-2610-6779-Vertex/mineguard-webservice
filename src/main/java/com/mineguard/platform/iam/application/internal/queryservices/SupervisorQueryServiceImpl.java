package com.mineguard.platform.iam.application.internal.queryservices;

import com.mineguard.platform.iam.application.queryservices.SupervisorQueryService;
import com.mineguard.platform.iam.domain.model.aggregates.Supervisor;
import com.mineguard.platform.iam.domain.model.aggregates.User;
import com.mineguard.platform.iam.domain.model.queries.GetAllSupervisorsQuery;
import com.mineguard.platform.iam.domain.model.queries.GetSupervisorByIdQuery;
import com.mineguard.platform.iam.domain.repositories.SupervisorRepository;
import com.mineguard.platform.iam.domain.repositories.UserRepository;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SupervisorQueryServiceImpl implements SupervisorQueryService {
    private final SupervisorRepository supervisorRepository;
    private final UserRepository userRepository;
    private final SecurityContextFacade securityContext;

    public SupervisorQueryServiceImpl(SupervisorRepository supervisorRepository, UserRepository userRepository,
                                      SecurityContextFacade securityContext) {
        this.supervisorRepository = supervisorRepository;
        this.userRepository = userRepository;
        this.securityContext = securityContext;
    }

    @Override
    public List<Supervisor> handle(GetAllSupervisorsQuery query) {
        var companyId = securityContext.currentCompanyId();
        if (companyId == null) return List.of();
        Set<Long> tenantUserIds = userRepository.findAll().stream()
                .filter(u -> companyId.equals(u.getCompanyId()))
                .map(User::getId)
                .collect(Collectors.toSet());
        return supervisorRepository.findAll().stream()
                .filter(s -> tenantUserIds.contains(s.getUserId()))
                .map(this::enrichFromUser)
                .toList();
    }

    @Override
    public Optional<Supervisor> handle(GetSupervisorByIdQuery query) {
        var companyId = securityContext.currentCompanyId();
        if (companyId == null) return Optional.empty();
        return supervisorRepository.findById(query.supervisorId())
                .filter(s -> s.getUserId() != null
                        && userRepository.findById(s.getUserId())
                                .map(u -> companyId.equals(u.getCompanyId()))
                                .orElse(false))
                .map(this::enrichFromUser);
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
