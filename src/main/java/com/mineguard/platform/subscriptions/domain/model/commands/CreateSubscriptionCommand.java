package com.mineguard.platform.subscriptions.domain.model.commands;
import java.time.LocalDateTime;
/** Command to create a new subscription. */
public record CreateSubscriptionCommand(Long userId, Long planId, Boolean autoRenew,
                                        String paymentMethodType, String paymentMethodDetails,
                                        LocalDateTime startDate, LocalDateTime endDate) {}
