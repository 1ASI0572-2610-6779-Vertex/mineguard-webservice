package com.mineguard.platform.subscriptions.application.internal.commandservices;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import com.mineguard.platform.subscriptions.application.commandservices.SubscriptionCommandService;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Subscription;
import com.mineguard.platform.subscriptions.domain.model.commands.*;
import com.mineguard.platform.subscriptions.domain.model.entities.Payment;
import com.mineguard.platform.subscriptions.domain.model.entities.PaymentMethod;
import com.mineguard.platform.subscriptions.domain.model.valueobjects.Money;
import com.mineguard.platform.subscriptions.domain.model.valueobjects.PaymentStatus;
import com.mineguard.platform.subscriptions.domain.repositories.PlanRepository;
import com.mineguard.platform.subscriptions.domain.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Service
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final SecurityContextFacade securityContext;
    public SubscriptionCommandServiceImpl(SubscriptionRepository subscriptionRepository, PlanRepository planRepository,
                                          SecurityContextFacade securityContext) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.securityContext = securityContext;
    }
    @Override
    public Result<Subscription, ApplicationError> handle(CreateSubscriptionCommand command) {
        if (command.userId() == null || command.planId() == null) {
            return Result.failure(ApplicationError.validationError("subscription", "userId and planId are required"));
        }
        var plan = planRepository.findById(command.planId())
                .orElse(null);
        if (plan == null) {
            return Result.failure(ApplicationError.notFound("Plan", String.valueOf(command.planId())));
        }
        var subscription = new Subscription(command.userId(), plan, command.autoRenew(),
                new PaymentMethod(command.paymentMethodType(), command.paymentMethodDetails()));
        subscription.setCompanyId(securityContext.currentCompanyId());
        subscription.setStartDate(command.startDate());
        subscription.setEndDate(command.endDate());
        subscription.activate();
        return Result.success(subscriptionRepository.save(subscription));
    }
    @Override
    public Result<Subscription, ApplicationError> handle(CancelSubscriptionCommand command) {
        return mutateSubscription(command.subscriptionId(), subscription -> subscription.cancel());
    }
    @Override
    public Result<Subscription, ApplicationError> handle(RenewSubscriptionCommand command) {
        return mutateSubscription(command.subscriptionId(), subscription -> subscription.renew());
    }
    @Override
    public Result<Subscription, ApplicationError> handle(ProcessPaymentCommand command) {
        var existing = subscriptionRepository.findById(command.subscriptionId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Subscription", String.valueOf(command.subscriptionId())));
        }
        var subscription = existing.get();
        var payment = new Payment(nextPaymentId(subscription), subscription.getPlan().getPrice(), PaymentStatus.SUCCESSFUL,
                LocalDateTime.now());
        subscription.processPayment(payment);
        return Result.success(subscriptionRepository.save(subscription));
    }
    @Override
    public Result<Subscription, ApplicationError> handle(RetryPaymentCommand command) {
        var existing = subscriptionRepository.findById(command.subscriptionId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Subscription", String.valueOf(command.subscriptionId())));
        }
        var subscription = existing.get();
        var payment = subscription.getPayments().stream()
                .filter(item -> item.getId() != null && item.getId().equals(command.paymentId()))
                .findFirst().orElse(null);
        if (payment == null) {
            return Result.failure(ApplicationError.notFound("Payment", String.valueOf(command.paymentId())));
        }
        payment.retry();
        return Result.success(subscriptionRepository.save(subscription));
    }
    @Override
    public Result<Subscription, ApplicationError> handle(UpdatePaymentMethodCommand command) {
        return mutateSubscription(command.subscriptionId(), subscription ->
                subscription.updatePaymentMethod(new PaymentMethod(command.type(), command.details())));
    }
    private Result<Subscription, ApplicationError> mutateSubscription(Long subscriptionId, java.util.function.Consumer<Subscription> mutation) {
        var existing = subscriptionRepository.findById(subscriptionId);
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Subscription", String.valueOf(subscriptionId)));
        }
        var subscription = existing.get();
        mutation.accept(subscription);
        return Result.success(subscriptionRepository.save(subscription));
    }
    private Long nextPaymentId(Subscription subscription) {
        return subscription.getPayments().stream()
                .map(Payment::getId)
                .filter(java.util.Objects::nonNull)
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L) + 1L;
    }
}
