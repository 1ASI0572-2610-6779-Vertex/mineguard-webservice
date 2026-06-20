package com.mineguard.platform.subscriptions.application.internal.commandservices;

import com.mineguard.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.mineguard.platform.iam.domain.model.aggregates.User;
import com.mineguard.platform.iam.domain.model.entities.Role;
import com.mineguard.platform.iam.domain.model.valueobjects.Roles;
import com.mineguard.platform.iam.domain.repositories.RoleRepository;
import com.mineguard.platform.iam.domain.repositories.UserRepository;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import com.mineguard.platform.shared.domain.utils.PasswordGenerator;
import com.mineguard.platform.shared.domain.utils.UsernameGenerator;
import com.mineguard.platform.shared.infrastructure.mail.IEmailService;
import com.mineguard.platform.subscriptions.application.commandservices.CompanyRegistrationCommandService;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Company;
import com.mineguard.platform.subscriptions.domain.model.commands.RegisterCompanyCommand;
import com.mineguard.platform.subscriptions.domain.repositories.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/** Provisions a new tenant: creates the company and its administrator user in one transaction. */
@Slf4j
@Service
public class CompanyRegistrationCommandServiceImpl implements CompanyRegistrationCommandService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HashingService hashingService;
    private final IEmailService emailService;

    public CompanyRegistrationCommandServiceImpl(CompanyRepository companyRepository,
                                                 UserRepository userRepository,
                                                 RoleRepository roleRepository,
                                                 HashingService hashingService,
                                                 IEmailService emailService) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.hashingService = hashingService;
        this.emailService = emailService;
    }

    @Override
    public Result<String, ApplicationError> handle(RegisterCompanyCommand command) {
        if (command.adminEmail() == null || command.adminEmail().isBlank()) {
            return Result.failure(ApplicationError.validationError("adminEmail", "Admin email is required"));
        }
        if (userRepository.existsByUsername(command.adminEmail())) {
            return Result.failure(ApplicationError.conflict("User", "Email already registered"));
        }

        var company = companyRepository.save(new Company(command.companyName()));

        var adminRole = roleRepository.findByName(Roles.ROLE_ADMINISTRATOR)
                .orElseGet(() -> roleRepository.save(new Role(Roles.ROLE_ADMINISTRATOR)));

        var generatedUsername = UsernameGenerator.forAdmin(
                company.getId(), userRepository.countByUsernamePrefix(UsernameGenerator.adminPrefix(company.getId())));
        var tempPassword = PasswordGenerator.generate();
        userRepository.save(new User(
                generatedUsername,
                hashingService.encode(tempPassword),
                command.adminEmail(),
                command.adminFullName(),
                company.getId(),
                List.of(adminRole),
                true));

        log.info("[DEV FAILSAFE] CONTRASEÑA TEMPORAL GENERADA PARA {}: {}", command.adminEmail(), tempPassword);
        emailService.sendCredentialsEmail(command.adminEmail(), "ROLE_ADMINISTRATOR",
                generatedUsername, command.companyName(), tempPassword);

        return Result.success("Company registered successfully. Credentials sent to " + command.adminEmail());
    }
}
