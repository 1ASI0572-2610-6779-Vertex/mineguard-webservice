package com.mineguard.platform.subscriptions.domain.model.commands;
/** Command to retry a failed payment. */
public record RetryPaymentCommand(Long subscriptionId, Long paymentId) {}
