package com.mineguard.platform.subscriptions.interfaces.rest.resources;
import java.time.LocalDateTime;
import java.util.List;
/** Subscription resource exposed to REST clients. */
public record SubscriptionResource(Long id, Long companyId, Long userId, PlanResource plan, String status,
                                   LocalDateTime startDate, LocalDateTime endDate, Boolean autoRenew,
                                   List<PaymentResource> payments, String paymentMethodType,
                                   String paymentMethodDetails) {}
