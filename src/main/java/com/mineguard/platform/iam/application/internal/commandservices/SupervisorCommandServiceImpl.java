package com.mineguard.platform.iam.application.internal.commandservices;

import com.mineguard.platform.iam.application.commandservices.SupervisorCommandService;
import com.mineguard.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.mineguard.platform.iam.domain.model.aggregates.Supervisor;
import com.mineguard.platform.iam.domain.model.aggregates.User;
import com.mineguard.platform.iam.domain.model.commands.CreateSupervisorCommand;
import com.mineguard.platform.iam.domain.model.commands.UpdateSupervisorCommand;
import com.mineguard.platform.iam.domain.model.entities.Role;
import com.mineguard.platform.iam.domain.model.valueobjects.Roles;
import com.mineguard.platform.iam.domain.repositories.RoleRepository;
import com.mineguard.platform.iam.domain.repositories.SupervisorRepository;
import com.mineguard.platform.iam.domain.repositories.UserRepository;
import com.mineguard.platform.monitoring.application.internal.AuditLogWriter;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import com.mineguard.platform.shared.domain.utils.PasswordGenerator;
import com.mineguard.platform.shared.domain.utils.UsernameGenerator;
import com.mineguard.platform.shared.infrastructure.mail.IEmailService;
import org.springframework.stereotype.Service;

import java.util.List;

/** Implements supervisor account management. */
@Service
public class SupervisorCommandServiceImpl implements SupervisorCommandService {
    private final SupervisorRepository supervisorRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HashingService hashingService;
    private final AuditLogWriter auditLogWriter;
    private final IEmailService emailService;

    public SupervisorCommandServiceImpl(SupervisorRepository supervisorRepository, UserRepository userRepository,
                                        RoleRepository roleRepository, HashingService hashingService,
                                        AuditLogWriter auditLogWriter, IEmailService emailService) {
        this.supervisorRepository = supervisorRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.hashingService = hashingService;
        this.auditLogWriter = auditLogWriter;
        this.emailService = emailService;
    }

    @Override
    public Result<Supervisor, ApplicationError> handle(CreateSupervisorCommand command) {
        if (supervisorRepository.existsByCorporateId(command.corporateId())) {
            return Result.failure(ApplicationError.conflict("Supervisor", "Corporate id already exists"));
        }
        var companyId = command.idCompany() != null ? command.idCompany() : 0L;
        var generatedUsername = UsernameGenerator.forSupervisor(
                companyId, userRepository.countByUsernamePrefix(UsernameGenerator.supervisorPrefix(companyId)));
        var role = roleRepository.findByName(Roles.ROLE_SUPERVISOR)
                .orElseGet(() -> roleRepository.save(new Role(Roles.ROLE_SUPERVISOR)));
        var tempPassword = PasswordGenerator.generate();
        var user = userRepository.save(new User(
                generatedUsername,
                hashingService.encode(tempPassword),
                command.email(), command.fullName(), command.idCompany(), List.of(role), true));
        if (command.email() != null && !command.email().isBlank()) {
            emailService.sendCredentialsEmail(command.email(), "ROLE_SUPERVISOR",
                    generatedUsername, command.fullName(), tempPassword);
        }
        var supervisor = new Supervisor(user.getId(), command.fullName(), command.corporateId(),
                command.email(), com.mineguard.platform.iam.domain.model.valueobjects.AccessStatus.ACTIVE);
        supervisor = supervisorRepository.save(supervisor);
        auditLogWriter.record("administrative", "monitoring.audit.entries.supervisorCreated.title",
                "monitoring.audit.entries.supervisorCreated.description",
                "{\"supervisorId\":" + supervisor.getId() + ",\"fullName\":\"" + supervisor.getFullName() + "\"}",
                "monitoring.audit.actors.adminGlobal");
        return Result.success(supervisor);
    }

    @Override
    public Result<Supervisor, ApplicationError> handle(UpdateSupervisorCommand command) {
        var existing = supervisorRepository.findById(command.id());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Supervisor", String.valueOf(command.id())));
        }
        var supervisor = existing.get();
        if (supervisor.getUserId() == null && command.username() != null && !command.username().isBlank()) {
            if (userRepository.existsByUsername(command.username())) {
                return Result.failure(ApplicationError.conflict("User", "Username already exists"));
            }
            var role = roleRepository.findByName(Roles.ROLE_SUPERVISOR)
                    .orElseGet(() -> roleRepository.save(new Role(Roles.ROLE_SUPERVISOR)));
            var user = userRepository.save(new User(command.username(),
                    hashingService.encode(command.password() == null ? "123456" : command.password()),
                    command.email(), command.fullName(), command.idCompany(), List.of(role)));
            supervisor.setUserId(user.getId());
        } else if (supervisor.getUserId() != null) {
            userRepository.findById(supervisor.getUserId()).ifPresent(user -> {
                if (command.username() != null) user.setUsername(command.username());
                if (command.password() != null && !command.password().isBlank()) user.setPassword(hashingService.encode(command.password()));
                if (command.email() != null) user.setEmail(command.email());
                if (command.fullName() != null) user.setFullName(command.fullName());
                if (command.idCompany() != null) user.setCompanyId(command.idCompany());
                userRepository.save(user);
            });
        }
        supervisor.updateInformation(command.fullName(), command.corporateId(), command.email(), command.accessStatus());
        var saved = supervisorRepository.save(supervisor);
        auditLogWriter.record("administrative", "monitoring.audit.entries.supervisorUpdated.title",
                "monitoring.audit.entries.supervisorUpdated.description",
                "{\"supervisorId\":" + saved.getId() + ",\"fullName\":\"" + saved.getFullName() + "\"}",
                "monitoring.audit.actors.adminGlobal");
        return Result.success(saved);
    }
}
