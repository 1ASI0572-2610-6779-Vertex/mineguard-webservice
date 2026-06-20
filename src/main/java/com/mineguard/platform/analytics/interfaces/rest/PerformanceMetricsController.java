package com.mineguard.platform.analytics.interfaces.rest;

/**
 * Eliminated — consolidated into {@link MobilePerformanceController}.
 *
 * <p>Mapping reference:
 * <ul>
 *   <li>{@code GET /performanceMetrics} → {@code GET /api/v1/drivers/{driverId}/performance-metrics}</li>
 * </ul>
 *
 * Delete this file once all frontend clients have migrated to the new route.
 */
@Deprecated(forRemoval = true)
class PerformanceMetricsController {
    private PerformanceMetricsController() {}
}
