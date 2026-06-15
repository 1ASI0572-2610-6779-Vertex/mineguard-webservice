package com.mineguard.platform.iam.application.internal.commandservices;

import com.mineguard.platform.iam.application.commandservices.AdministratorCommandService;
import com.mineguard.platform.iam.domain.model.aggregates.Administrator;
import com.mineguard.platform.iam.domain.model.commands.CreateAdministratorCommand;
import com.mineguard.platform.iam.domain.model.commands.UpdateAdministratorCommand;
import com.mineguard.platform.iam.domain.repositories.AdministratorRepository;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class AdministratorCommandServiceImpl implements AdministratorCommandService {
    private final AdministratorRepository administratorRepository;

    public AdministratorCommandServiceImpl(AdministratorRepository administratorRepository) {
        this.administratorRepository = administratorRepository;
    }

    @Override
    public Result<Administrator, ApplicationError> handle(CreateAdministratorCommand command) {
        if (administratorRepository.existsByEmail(command.email())) {
            return Result.failure(ApplicationError.conflict("Administrator", "Email already exists"));
        }
        var administrator = administratorRepository.save(new Administrator(command));
        return Result.success(administrator);
    }

    @Override
    public Result<Administrator, ApplicationError> handle(UpdateAdministratorCommand command) {
        var existing = administratorRepository.findById(command.id());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Administrator", String.valueOf(command.id())));
        }
        var administrator = existing.get();
        administrator.updateInformation(command.fullName(), command.email(), command.accessStatus());
        return Result.success(administratorRepository.save(administrator));
    }
}