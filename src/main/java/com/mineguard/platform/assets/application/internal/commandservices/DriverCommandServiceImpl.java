package com.mineguard.platform.assets.application.internal.commandservices;

import com.mineguard.platform.assets.application.commandservices.DriverCommandService;
import com.mineguard.platform.assets.domain.model.aggregates.Driver;
import com.mineguard.platform.assets.domain.model.commands.CreateDriverCommand;
import com.mineguard.platform.assets.domain.model.commands.UpdateDriverCommand;
import com.mineguard.platform.assets.domain.model.valueobjects.ShiftStatus;
import com.mineguard.platform.assets.domain.repositories.DriverRepository;
import com.mineguard.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.mineguard.platform.iam.domain.model.aggregates.User;
import com.mineguard.platform.iam.domain.model.entities.Role;
import com.mineguard.platform.iam.domain.model.valueobjects.Roles;
import com.mineguard.platform.iam.domain.repositories.RoleRepository;
import com.mineguard.platform.iam.domain.repositories.UserRepository;
import com.mineguard.platform.monitoring.application.internal.AuditLogWriter;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import com.mineguard.platform.shared.domain.utils.PasswordGenerator;
import com.mineguard.platform.shared.domain.utils.UsernameGenerator;
import com.mineguard.platform.shared.infrastructure.mail.IEmailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverCommandServiceImpl implements DriverCommandService {
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HashingService hashingService;
    private final AuditLogWriter auditLogWriter;
    private final IEmailService emailService;

    public DriverCommandServiceImpl(DriverRepository driverRepository, UserRepository userRepository,
                                    RoleRepository roleRepository, HashingService hashingService,
                                    AuditLogWriter auditLogWriter, IEmailService emailService) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.hashingService = hashingService;
        this.auditLogWriter = auditLogWriter;
        this.emailService = emailService;
    }

    @Override
    public Result<Driver, ApplicationError> handle(CreateDriverCommand command) {
        var companyId = command.idCompany() != null ? command.idCompany() : 0L;
        var generatedUsername = UsernameGenerator.forDriver(
                companyId, userRepository.countByUsernamePrefix(UsernameGenerator.driverPrefix(companyId)));
        var role = roleRepository.findByName(Roles.ROLE_DRIVER)
                .orElseGet(() -> roleRepository.save(new Role(Roles.ROLE_DRIVER)));
        var tempPassword = PasswordGenerator.generate();
        var user = userRepository.save(new User(generatedUsername, hashingService.encode(tempPassword),
                command.email(), command.fullName(), command.idCompany(), List.of(role), true));
        if (command.email() != null && !command.email().isBlank()) {
            emailService.sendCredentialsEmail(command.email(), "ROLE_DRIVER",
                    generatedUsername, command.fullName(), tempPassword);
        }
        var driver = new Driver(command.fullName(), operatorId(command.licenseNumber()), command.licenseNumber(),
                command.workShift(), ShiftStatus.ON_SHIFT, null, user.getId());
        driver.setCompanyId(command.idCompany());
        driver = driverRepository.save(driver);
        auditLogWriter.record("administrative", "monitoring.audit.entries.driverCreated.title",
                "monitoring.audit.entries.driverCreated.description",
                "{\"driverId\":" + driver.getId() + ",\"fullName\":\"" + driver.getFullName() + "\"}",
                "monitoring.audit.actors.adminGlobal");
        return Result.success(driver);
    }

    @Override
    public Result<Driver, ApplicationError> handle(UpdateDriverCommand command) {
        var existing = driverRepository.findById(command.id());
        if (existing.isEmpty()) return Result.failure(ApplicationError.notFound("Driver", String.valueOf(command.id())));
        var driver = existing.get();
        if (driver.getUserId() == null && command.username() != null && !command.username().isBlank()) {
            if (userRepository.existsByUsername(command.username())) {
                return Result.failure(ApplicationError.conflict("User", "Username already exists"));
            }
            var role = roleRepository.findByName(Roles.ROLE_DRIVER)
                    .orElseGet(() -> roleRepository.save(new Role(Roles.ROLE_DRIVER)));
            var user = userRepository.save(new User(command.username(),
                    hashingService.encode(command.password() == null ? "123456" : command.password()),
                    command.email(), command.fullName(), command.idCompany(), List.of(role)));
            driver.setUserId(user.getId());
        } else if (driver.getUserId() != null) {
            userRepository.findById(driver.getUserId()).ifPresent(user -> {
                if (command.username() != null) user.setUsername(command.username());
                if (command.password() != null && !command.password().isBlank()) user.setPassword(hashingService.encode(command.password()));
                if (command.email() != null) user.setEmail(command.email());
                if (command.fullName() != null) user.setFullName(command.fullName());
                if (command.idCompany() != null) user.setCompanyId(command.idCompany());
                userRepository.save(user);
            });
        }
        if (command.fullName() != null) driver.setFullName(command.fullName());
        if (command.licenseNumber() != null) {
            driver.setLicense(command.licenseNumber());
            driver.setOperatorId(operatorId(command.licenseNumber()));
        }
        if (command.workShift() != null) driver.setSpecialty(command.workShift());
        var saved = driverRepository.save(driver);
        auditLogWriter.record("administrative", "monitoring.audit.entries.driverUpdated.title",
                "monitoring.audit.entries.driverUpdated.description",
                "{\"driverId\":" + saved.getId() + ",\"fullName\":\"" + saved.getFullName() + "\"}",
                "monitoring.audit.actors.adminGlobal");
        return Result.success(saved);
    }

    private String operatorId(String license) {
        if (license == null || license.isBlank()) return null;
        return "OP-" + license.replaceAll("[^0-9]", "");
    }
}
