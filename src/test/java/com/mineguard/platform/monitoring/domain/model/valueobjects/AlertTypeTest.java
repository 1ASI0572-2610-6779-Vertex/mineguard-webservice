package com.mineguard.platform.monitoring.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertTypeTest {

    @Test
    void shouldSerializeCorrectly() {
        assertEquals("fatigue", AlertType.FATIGUE.toSerialized());
    }

    @Test
    void shouldDeserializeAliasesCorrectly() {
        assertEquals(AlertType.FATIGUE, AlertType.fromSerialized("fatigue_risk"));
        assertEquals(AlertType.PROXIMITY_COLLISION, AlertType.fromSerialized("collision"));
        assertEquals(AlertType.PROXIMITY, AlertType.fromSerialized("speed_excess"));
        assertEquals(AlertType.IMMINENT_COLLISION, AlertType.fromSerialized("imminent_collision"));
    }

    @Test
    void shouldReturnDefaultWhenNull() {
        assertEquals(AlertType.COLLISION, AlertType.fromSerialized(null));
    }
}