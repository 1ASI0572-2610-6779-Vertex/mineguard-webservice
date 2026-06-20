package com.mineguard.platform.iam.application.commandservices;

import com.mineguard.platform.iam.domain.model.aggregates.User;
import com.mineguard.platform.iam.domain.model.commands.ChangePasswordCommand;
import com.mineguard.platform.iam.domain.model.commands.MobileSignInCommand;
import com.mineguard.platform.iam.domain.model.commands.ResetPasswordCommand;
import com.mineguard.platform.iam.domain.model.commands.SignInCommand;
import com.mineguard.platform.iam.domain.model.commands.SignUpCommand;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;

/** Command service port for user authentication and registration. */
public interface UserCommandService {
    Result<ImmutablePair<User, String>, ApplicationError> handle(SignInCommand command);
    Result<ImmutablePair<User, String>, ApplicationError> handle(MobileSignInCommand command);
    Result<User, ApplicationError> handle(SignUpCommand command);
    Result<User, ApplicationError> handle(ChangePasswordCommand command);
    /** Always returns success to avoid disclosing whether the email is registered. */
    Result<Void, ApplicationError> handle(ResetPasswordCommand command);
}
