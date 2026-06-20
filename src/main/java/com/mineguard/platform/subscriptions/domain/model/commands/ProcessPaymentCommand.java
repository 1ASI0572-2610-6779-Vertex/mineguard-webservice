package com.mineguard.platform.subscriptions.domain.model.commands;
/** Command to process a payment for a subscription. */
public record ProcessPaymentCommand(Long subscriptionId) {}
