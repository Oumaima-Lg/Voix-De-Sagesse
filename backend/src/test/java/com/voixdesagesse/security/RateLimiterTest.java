package com.voixdesagesse.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.voixdesagesse.exception.VoixDeSagesseException;
import com.voixdesagesse.security.RateLimiter.Limit;

class RateLimiterTest {

    private static final String IP = "203.0.113.7";

    private MutableClock clock;
    private RateLimiter limiter;

    @BeforeEach
    void setUp() {
        clock = new MutableClock(Instant.parse("2026-01-01T10:00:00Z"));
        limiter = new RateLimiter(Map.of(Limit.EMAIL_CODE, 3, Limit.PICTURE_UPLOAD, 2), clock);
    }

    @Test
    @DisplayName("Codes par email : refusés au-delà de la limite horaire")
    void refusesAboveLimit() throws VoixDeSagesseException {
        for (int i = 0; i < 3; i++) limiter.check(Limit.EMAIL_CODE, IP);

        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class, () -> limiter.check(Limit.EMAIL_CODE, IP));
        assertEquals("TOO_MANY_CODE_REQUESTS", exception.getMessage());
    }

    @Test
    @DisplayName("Codes par email : de nouveau possibles après une heure")
    void allowsAgainAfterWindow() throws VoixDeSagesseException {
        for (int i = 0; i < 3; i++) limiter.check(Limit.EMAIL_CODE, IP);

        clock.advance(Duration.ofMinutes(61));

        assertDoesNotThrow(() -> limiter.check(Limit.EMAIL_CODE, IP));
    }

    @Test
    @DisplayName("Chaque adresse IP et chaque limite a son propre compteur")
    void countersAreSeparate() throws VoixDeSagesseException {
        for (int i = 0; i < 3; i++) limiter.check(Limit.EMAIL_CODE, IP);

        assertDoesNotThrow(() -> limiter.check(Limit.EMAIL_CODE, "198.51.100.2"));
        assertDoesNotThrow(() -> limiter.check(Limit.PICTURE_UPLOAD, IP));
    }

    @Test
    @DisplayName("Photos : refusées au-delà de la limite horaire")
    void limitsUploads() throws VoixDeSagesseException {
        limiter.check(Limit.PICTURE_UPLOAD, "7");
        limiter.check(Limit.PICTURE_UPLOAD, "7");

        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class, () -> limiter.check(Limit.PICTURE_UPLOAD, "7"));
        assertEquals("TOO_MANY_UPLOADS", exception.getMessage());
    }
}
