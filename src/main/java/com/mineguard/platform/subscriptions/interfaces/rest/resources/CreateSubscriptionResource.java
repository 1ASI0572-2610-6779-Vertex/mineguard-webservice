package com.mineguard.platform.subscriptions.interfaces.rest.resources;
import java.time.LocalDateTime;
/** Request body for creating a subscription. */
public record CreateSubscriptionResource(Long userId, Long planId, Boolean autoRenew,
                                        String paymentMethodType, String paymentMethodDetails,
                                        LocalDateTime startDate, LocalDateTime endDate) {}
