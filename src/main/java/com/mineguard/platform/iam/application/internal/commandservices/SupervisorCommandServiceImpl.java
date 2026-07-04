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
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** Implements supervisor account management. */
@Service
public class SupervisorCommandServiceImpl implements SupervisorCommandService {
    private final SupervisorRepository supervisorRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HashingService hashingService;
    private final AuditLogWriter auditLogWriter;
    private final IEmailService emailService;
    private final SecurityContextFacade securityContext;

    public SupervisorCommandServiceImpl(SupervisorRepository supervisorRepository, UserRepository userRepository,
                                        RoleRepository roleRepository, HashingService hashingService,
                                        AuditLogWriter auditLogWriter, IEmailService emailService,
                                        SecurityContextFacade securityContext) {
        this.supervisorRepository = supervisorRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.hashingService = hashingService;
        this.auditLogWriter = auditLogWriter;
        this.emailService = emailService;
        this.securityContext = securityContext;
    }

    @Override
    public Result<Supervisor, ApplicationError> handle(CreateSupervisorCommand command) {
        if (supervisorRepository.existsByCorporateId(command.corporateId())) {
            return Result.failure(ApplicationError.conflict("Supervisor", "Corporate id already exists"));
        }
        var companyId = command.companyId() != null ? command.companyId() : 0L;
        var generatedUsername = UsernameGenerator.forSupervisor(
                companyId, userRepository.countByUsernamePrefix(UsernameGenerator.supervisorPrefix(companyId)));
        var role = roleRepository.findByName(Roles.ROLE_SUPERVISOR)
                .orElseGet(() -> roleRepository.save(new Role(Roles.ROLE_SUPERVISOR)));
        var tempPassword = PasswordGenerator.generate();
        var user = userRepository.save(new User(
                generatedUsername,
                hashingService.encode(tempPassword),
                command.email(), command.fullName(), companyId, List.of(role), true));
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
        var callerCompanyId = securityContext.currentCompanyId();
        Optional<User> ownerUser = supervisor.getUserId() == null
                ? Optional.empty() : userRepository.findById(supervisor.getUserId());
        if (callerCompanyId == null || ownerUser.isEmpty() || !callerCompanyId.equals(ownerUser.get().getCompanyId())) {
            return Result.failure(ApplicationError.notFound("Supervisor", String.valueOf(command.id())));
        }
        var user = ownerUser.get();
        if (command.username() != null) user.setUsername(command.username());
        if (command.password() != null && !command.password().isBlank()) user.setPassword(hashingService.encode(command.password()));
        if (command.email() != null) user.setEmail(command.email());
        if (command.fullName() != null) user.setFullName(command.fullName());
        userRepository.save(user);
        supervisor.updateInformation(command.fullName(), command.corporateId(), command.email(), command.accessStatus());
        var saved = supervisorRepository.save(supervisor);
        auditLogWriter.record("administrative", "monitoring.audit.entries.supervisorUpdated.title",
                "monitoring.audit.entries.supervisorUpdated.description",
                "{\"supervisorId\":" + saved.getId() + ",\"fullName\":\"" + saved.getFullName() + "\"}",
                "monitoring.audit.actors.adminGlobal");
        return Result.success(saved);
    }
}
