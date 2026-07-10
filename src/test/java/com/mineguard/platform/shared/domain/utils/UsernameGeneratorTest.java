package com.mineguard.platform.shared.domain.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsernameGeneratorTest {

    @Test
    void forAdminShouldGenerateAdminUsername() {
        String username = UsernameGenerator.forAdmin(3L, 0);

        assertEquals("ADM-3-001", username);
    }

    @Test
    void forSupervisorShouldGenerateSupervisorUsername() {
        String username = UsernameGenerator.forSupervisor(3L, 4);

        assertEquals("SUP-3-005", username);
    }

    @Test
    void forDriverShouldGenerateDriverUsername() {
        String username = UsernameGenerator.forDriver(3L, 9);

        assertEquals("CDT-3-010", username);
    }
}