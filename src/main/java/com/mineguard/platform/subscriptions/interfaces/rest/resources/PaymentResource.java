package com.mineguard.platform.subscriptions.interfaces.rest.resources;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/** Payment resource exposed by subscriptions endpoints. */
public record PaymentResource(Long id, BigDecimal amount, String currency, String status, LocalDateTime createdAt) {}
