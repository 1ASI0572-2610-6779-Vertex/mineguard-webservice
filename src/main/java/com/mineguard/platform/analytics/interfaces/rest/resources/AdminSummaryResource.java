package com.mineguard.platform.analytics.interfaces.rest.resources;
public record AdminSummaryResource(Long id, int activeSensors, int totalSensors, int lockedAccounts, int registeredAssets) {}
