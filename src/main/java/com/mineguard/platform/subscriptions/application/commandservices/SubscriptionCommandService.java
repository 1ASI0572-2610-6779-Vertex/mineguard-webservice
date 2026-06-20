package com.mineguard.platform.subscriptions.application.commandservices;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Subscription;
import com.mineguard.platform.subscriptions.domain.model.commands.*;
public interface SubscriptionCommandService {
    Result<Subscription, ApplicationError> handle(CreateSubscriptionCommand command);
    Result<Subscription, ApplicationError> handle(CancelSubscriptionCommand command);
    Result<Subscription, ApplicationError> handle(RenewSubscriptionCommand command);
    Result<Subscription, ApplicationError> handle(ProcessPaymentCommand command);
    Result<Subscription, ApplicationError> handle(RetryPaymentCommand command);
    Result<Subscription, ApplicationError> handle(UpdatePaymentMethodCommand command);
}
