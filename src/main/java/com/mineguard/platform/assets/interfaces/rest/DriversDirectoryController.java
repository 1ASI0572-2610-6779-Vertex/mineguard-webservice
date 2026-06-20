package com.mineguard.platform.assets.interfaces.rest;

/**
 * Eliminated — consolidated into {@link DriversController#getAll(String)}.
 * The directory view is now served by {@code GET /api/v1/drivers?view=directory}.
 *
 * <p>This class is intentionally left as a package marker.
 * Delete this file once all frontend clients have migrated to the new route.
 */
@Deprecated(forRemoval = true)
class DriversDirectoryController {
    private DriversDirectoryController() {}
}
