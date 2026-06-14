package com.mineguard.platform.iam.application.internal.commandservices;

import com.mineguard.platform.iam.application.commandservices.UserCommandService;
import com.mineguard.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.mineguard.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.mineguard.platform.iam.domain.model.aggregates.User;
import com.mineguard.platform.iam.domain.model.commands.MobileSignInCommand;
import com.mineguard.platform.iam.domain.model.commands.SignInCommand;
import com.mineguard.platform.iam.domain.model.commands.SignUpCommand;
import com.mineguard.platform.iam.domain.repositories.RoleRepository;
import com.mineguard.platform.iam.domain.repositories.UserRepository;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Implements authentication and registration use cases. */
@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;

    public UserCommandServiceImpl(UserRepository userRepository, HashingService hashingService,
                                  TokenService tokenService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
    }

    @Override
    public Result<ImmutablePair<User, String>, ApplicationError> handle(SignInCommand command) {
        return authenticate(command.username(), command.password());
    }

    @Override
    public Result<ImmutablePair<User, String>, ApplicationError> handle(MobileSignInCommand command) {
        return authenticate(command.workerId(), command.password());
    }

    private Result<ImmutablePair<User, String>, ApplicationError> authenticate(String username, String password) {
        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return Result.failure(ApplicationError.notFound("User", username));
        }
        if (!hashingService.matches(password, user.get().getPassword())) {
            return Result.failure(ApplicationError.validationError("credentials", "Invalid username or password"));
        }
        var token = tokenService.generateToken(user.get().getUsername());
        return Result.success(ImmutablePair.of(user.get(), token));
    }

    @Override
    public Result<User, ApplicationError> handle(SignUpCommand command) {
        if (userRepository.existsByUsername(command.username())) {
            return Result.failure(ApplicationError.conflict("User", "Username already exists"));
        }
        var roles = command.roles().stream()
                .map(role -> roleRepository.findByName(role.getName()))
                .toList();
        if (roles.stream().anyMatch(Optional::isEmpty)) {
            return Result.failure(ApplicationError.notFound("Role", "one or more role names"));
        }
        var resolvedRoles = roles.stream().map(Optional::get).toList();
        var user = new User(command.username(), hashingService.encode(command.password()),
                command.email(), command.fullName(), resolvedRoles);
        userRepository.save(user);
        return userRepository.findByUsername(command.username())
                .<Result<User, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.unexpected("sign-up", "Created user could not be reloaded")));
    }
}
