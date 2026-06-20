package com.mineguard.platform.subscriptions.interfaces.rest.resources;
import java.math.BigDecimal;
/** Public plan resource. */
public record PlanResource(Long id, String name, BigDecimal priceAmount, String priceCurrency, String billingCycle) {}
