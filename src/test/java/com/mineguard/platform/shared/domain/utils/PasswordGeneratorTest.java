package com.mineguard.platform.shared.domain.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    @Test
    void generateShouldReturnPasswordWithTenCharacters() {
        String password = PasswordGenerator.generate();

        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    void generateShouldContainUppercaseLowercaseDigitAndSymbol() {
        String password = PasswordGenerator.generate();

        assertTrue(password.matches(".*[A-Z].*"));
        assertTrue(password.matches(".*[a-z].*"));
        assertTrue(password.matches(".*[0-9].*"));
        assertTrue(password.matches(".*[@#$!].*"));
    }
}