package com.mineguard.platform.monitoring.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardiacStatusTest {

    @Test
    void shouldReturnNormalStatus() {
        assertEquals(CardiacStatus.NORMAL, CardiacStatus.fromBpm(75));
    }

    @Test
    void shouldReturnWarningStatus() {
        assertEquals(CardiacStatus.WARNING, CardiacStatus.fromBpm(105));
        assertEquals(CardiacStatus.WARNING, CardiacStatus.fromBpm(50));
    }

    @Test
    void shouldReturnCriticalStatus() {
        assertEquals(CardiacStatus.CRITICAL, CardiacStatus.fromBpm(130));
        assertEquals(CardiacStatus.CRITICAL, CardiacStatus.fromBpm(40));
    }
}