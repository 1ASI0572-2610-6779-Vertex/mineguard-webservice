package com.mineguard.platform.monitoring.interfaces.rest;

/**
 * Eliminated — consolidated into {@link MobileAlertsController}.
 *
 * <p>Mapping reference:
 * <ul>
 *   <li>{@code GET  /operationalAlerts}    → {@code GET  /api/v1/alerts?view=operational}</li>
 *   <li>{@code PUT  /operationalAlerts/{id}} → {@code PUT  /api/v1/alerts/{alertId}}</li>
 * </ul>
 *
 * Delete this file once all frontend clients have migrated to the new routes.
 */
@Deprecated(forRemoval = true)
class OperationalAlertsController {
    private OperationalAlertsController() {}
}
