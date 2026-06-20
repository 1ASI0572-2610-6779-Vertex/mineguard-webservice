package com.mineguard.platform.subscriptions.domain.model.commands;
/** Command to update the active payment method for a subscription. */
public record UpdatePaymentMethodCommand(Long subscriptionId, String type, String details) {}
