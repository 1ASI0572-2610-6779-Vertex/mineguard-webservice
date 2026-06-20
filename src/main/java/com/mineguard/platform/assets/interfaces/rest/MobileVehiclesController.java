package com.mineguard.platform.assets.interfaces.rest;

/**
 * Eliminated — consolidated into {@link VehiclesInventoryController#getAll(String)}.
 * The mobile vehicle list is now served by {@code GET /api/v1/vehicles}.
 *
 * <p>This class is intentionally left as a package marker.
 * Delete this file once all frontend clients have migrated to the new route.
 */
@Deprecated(forRemoval = true)
class MobileVehiclesController {
    private MobileVehiclesController() {}
}
