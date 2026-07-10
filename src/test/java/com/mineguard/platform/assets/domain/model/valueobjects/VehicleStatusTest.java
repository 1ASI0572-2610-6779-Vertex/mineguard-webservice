package com.mineguard.platform.assets.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleStatusTest {

    @Test
    void shouldSerializeOperationalCorrectly() {
        assertEquals("operational", VehicleStatus.OPERATIONAL.toSerialized());
    }

    @Test
    void shouldDeserializeAliasesCorrectly() {
        assertEquals(VehicleStatus.OPERATIONAL, VehicleStatus.fromSerialized("active"));
        assertEquals(VehicleStatus.IN_TRANSIT, VehicleStatus.fromSerialized("in_use"));
        assertEquals(VehicleStatus.RESTRICTED_ROUTE, VehicleStatus.fromSerialized("restricted"));
    }

    @Test
    void shouldReturnOperationalForNullOrUnknownValues() {
        assertEquals(VehicleStatus.OPERATIONAL, VehicleStatus.fromSerialized(null));
        assertEquals(VehicleStatus.OPERATIONAL, VehicleStatus.fromSerialized("anything"));
    }
}