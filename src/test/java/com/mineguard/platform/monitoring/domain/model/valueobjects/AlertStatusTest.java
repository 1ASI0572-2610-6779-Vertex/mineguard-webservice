package com.mineguard.platform.monitoring.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertStatusTest {

    @Test
    void shouldSerializeCorrectly() {
        assertEquals("active", AlertStatus.ACTIVE.toSerialized());
    }

    @Test
    void shouldDeserializeAliasesCorrectly() {
        assertEquals(AlertStatus.ACTIVE, AlertStatus.fromSerialized("open"));
        assertEquals(AlertStatus.RESOLVED, AlertStatus.fromSerialized("closed"));
        assertEquals(AlertStatus.FALSE_ALARM, AlertStatus.fromSerialized("false_alarm"));
    }
}