package com.mineguard.platform.subscriptions.domain.model.commands;
/** Command to cancel an existing subscription. */
public record CancelSubscriptionCommand(Long subscriptionId) {}
