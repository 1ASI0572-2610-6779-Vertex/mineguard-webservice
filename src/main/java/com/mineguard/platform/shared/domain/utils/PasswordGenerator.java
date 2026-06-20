package com.mineguard.platform.shared.domain.utils;

import java.security.SecureRandom;

/** Generates cryptographically-random temporary passwords. */
public final class PasswordGenerator {

    private static final String UPPER   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER   = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS  = "0123456789";
    private static final String SYMBOLS = "@#$!";
    private static final String ALL     = UPPER + LOWER + DIGITS + SYMBOLS;

    private static final int LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordGenerator() {}

    /**
     * Returns a 10-character password that always contains at least one uppercase letter,
     * one lowercase letter, one digit, and one symbol — satisfying most password policies.
     */
    public static String generate() {
        char[] password = new char[LENGTH];
        // Guarantee at least one char from each mandatory group
        password[0] = UPPER.charAt(RANDOM.nextInt(UPPER.length()));
        password[1] = LOWER.charAt(RANDOM.nextInt(LOWER.length()));
        password[2] = DIGITS.charAt(RANDOM.nextInt(DIGITS.length()));
        password[3] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        for (int i = 4; i < LENGTH; i++) {
            password[i] = ALL.charAt(RANDOM.nextInt(ALL.length()));
        }
        // Fisher-Yates shuffle so the mandatory chars aren't always at the front
        for (int i = LENGTH - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char tmp = password[i];
            password[i] = password[j];
            password[j] = tmp;
        }
        return new String(password);
    }
}
