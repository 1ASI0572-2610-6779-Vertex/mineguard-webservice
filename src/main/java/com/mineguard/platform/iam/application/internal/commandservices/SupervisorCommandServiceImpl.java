package com.mineguard.platform.iam.application.internal.commandservices;

import com.mineguard.platform.iam.application.commandservices.SupervisorCommandService;
import com.mineguard.platform.iam.domain.model.aggregates.Supervisor;
import com.mineguard.platform.iam.domain.model.commands.CreateSupervisorCommand;
import com.mineguard.platform.iam.domain.model.commands.UpdateSupervisorCommand;
import com.mineguard.platform.iam.domain.repositories.SupervisorRepository;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

/** Implements supervisor account management. */
@Service
public class SupervisorCommandServiceImpl implements SupervisorCommandService {
    private final SupervisorRepository supervisorRepository;

    public SupervisorCommandServiceImpl(SupervisorRepository supervisorRepository) {
        this.supervisorRepository = supervisorRepository;
    }

    @Override
    public Result<Supervisor, ApplicationError> handle(CreateSupervisorCommand command) {
        if (supervisorRepository.existsByCorporateId(command.corporateId())) {
            return Result.failure(ApplicationError.conflict("Supervisor", "Corporate id already exists"));
        }
        var supervisor = supervisorRepository.save(new Supervisor(command));
        return Result.success(supervisor);
    }

    @Override
    public Result<Supervisor, ApplicationError> handle(UpdateSupervisorCommand command) {
        var existing = supervisorRepository.findById(command.id());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Supervisor", String.valueOf(command.id())));
        }
        var supervisor = existing.get();
        supervisor.updateInformation(command.fullName(), command.corporateId(), command.email(), command.accessStatus());
        return Result.success(supervisorRepository.save(supervisor));
    }
}
